// Global window types for session handling
declare global {
  interface Window {
    $toast: (message: string, type?: 'success' | 'error' | 'warning' | 'info', title?: string) => void;
    globalSessionExpiredHandler: () => void;
  }
}

export {};
