import { Component, type PropsWithChildren, type ReactNode } from 'react'
import { StatusPanel } from '../shared/components/StatusPanel'

interface State { hasError: boolean }

export class FeatureErrorBoundary extends Component<PropsWithChildren, State> {
  state: State = { hasError: false }

  static getDerivedStateFromError(): State { return { hasError: true } }

  render(): ReactNode {
    if (this.state.hasError) {
      return <StatusPanel kind="error" title="Khu vực này đang gặp sự cố" description="Vui lòng tải lại khu vực để tiếp tục." onRetry={() => this.setState({ hasError: false })} />
    }
    return this.props.children
  }
}
