"""Repair reversible UTF-8 mojibake in local demo data; backup changed cells first."""
import json
import subprocess
from pathlib import Path

MYSQL = "C:/xampp/mysql/bin/mysql.exe"
ARGS = [MYSQL, "-h", "127.0.0.1", "-P", "3307", "-u", "root",
        "--default-character-set=utf8mb4", "--batch", "--raw", "-N", "hospital_db"]


def query(sql):
    result = subprocess.run(ARGS, input=sql.encode("utf-8"), capture_output=True, check=True)
    return result.stdout.decode("utf-8").splitlines()


def repair(value):
    for _ in range(3):
        for encoding in ("latin1", "cp1252"):
            try:
                candidate = value.encode(encoding).decode("utf-8")
            except (UnicodeEncodeError, UnicodeDecodeError):
                continue
            if candidate != value:
                value = candidate
                break
        else:
            break
    return value


def main():
    columns = query("SELECT TABLE_NAME,COLUMN_NAME FROM information_schema.COLUMNS "
                    "WHERE TABLE_SCHEMA='hospital_db' AND DATA_TYPE IN ('varchar','text','longtext')")
    changes = []
    for entry in columns:
        table, column = entry.split("\t")
        # Credentials and identifiers are never modified.
        if column in {"password", "email", "role", "day_of_week", "status"}:
            continue
        rows = query(f"SELECT JSON_OBJECT('id',id,'value',`{column}`) FROM `{table}`")
        for row in rows:
            cell = json.loads(row)
            value = cell["value"]
            if not isinstance(value, str):
                continue
            fixed = repair(value)
            if fixed != value:
                changes.append(dict(table=table, column=column, id=cell["id"], before=value, after=fixed))
    if not changes:
        print("No reversible encoding errors found.")
        return
    backup = Path("target/vietnamese-encoding-backup.json")
    backup.parent.mkdir(exist_ok=True)
    if backup.exists():
        raise RuntimeError("Backup exists; preserve it before running another repair.")
    backup.write_text(json.dumps(changes, ensure_ascii=False, indent=2), encoding="utf-8")
    updates = ["START TRANSACTION;"]
    for change in changes:
        # Hex strings avoid shell escaping and client-codepage conversions.
        updates.append(f"UPDATE `{change['table']}` SET `{change['column']}`="
                       f"CONVERT(0x{change['after'].encode('utf-8').hex()} USING utf8mb4) "
                       f"WHERE id={int(change['id'])} AND BINARY `{change['column']}`="
                       f"0x{change['before'].encode('utf-8').hex()};")
    updates.append("COMMIT;")
    query("\n".join(updates))
    print(f"Repaired {len(changes)} cells. Original values saved in {backup}.")


if __name__ == "__main__":
    main()
