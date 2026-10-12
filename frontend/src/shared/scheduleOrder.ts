const weekdays = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']

export function compareSchedules(
  a: { dayOfWeek: string; startTime: string; endTime: string },
  b: { dayOfWeek: string; startTime: string; endTime: string },
) {
  return weekdays.indexOf(a.dayOfWeek) - weekdays.indexOf(b.dayOfWeek)
    || a.startTime.localeCompare(b.startTime)
    || a.endTime.localeCompare(b.endTime)
}
