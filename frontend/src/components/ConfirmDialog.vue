<template>
  <div class="confirm-dialog-overlay">
    <div class="confirm-dialog">
      <div class="confirm-icon">
        <i class="fas fa-exclamation-triangle"></i>
      </div>
      <div class="confirm-message">{{ message }}</div>
      <div class="confirm-actions">
        <button class="btn btn-cancel" @click="$emit('cancel')">Hủy</button>
        <button class="btn btn-confirm" @click="$emit('confirm')">Đồng ý</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ConfirmDialog',
  props: {
    message: { type: String, required: true }
  }
}
</script>

<style scoped>
.confirm-dialog-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.38);
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.confirm-dialog {
  background: #fff;
  border-radius: 22px;
  box-shadow: 0 10px 40px 0 rgba(0,0,0,0.22), 0 2px 8px rgba(0,0,0,0.10);
  padding: 38px 32px 28px 32px;
  min-width: 340px;
  max-width: 92vw;
  text-align: center;
  animation: popIn 0.22s cubic-bezier(.4,1.4,.6,1);
  position: relative;
}
.confirm-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 18px;
}
.confirm-icon i {
  color: #f59e42;
  font-size: 38px;
  filter: drop-shadow(0 2px 6px rgba(245,158,66,0.18));
}
.confirm-message {
  font-size: 1.22rem;
  font-weight: 600;
  margin-bottom: 32px;
  color: #222;
  line-height: 1.5;
}
.confirm-actions {
  display: flex;
  justify-content: center;
  gap: 22px;
}
.btn {
  min-width: 110px;
  padding: 10px 0;
  border-radius: 8px;
  font-size: 1.08rem;
  font-weight: 700;
  border: none;
  cursor: pointer;
  transition: background 0.18s, color 0.18s, box-shadow 0.18s;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.btn-cancel {
  background: #f3f4f6;
  color: #374151;
}
.btn-cancel:hover {
  background: #e5e7eb;
}
.btn-confirm {
  background: #ef4444;
  color: #fff;
  box-shadow: 0 2px 8px rgba(239,68,68,0.10);
}
.btn-confirm:hover {
  background: #dc2626;
}
@keyframes popIn {
  from { transform: scale(0.92); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}
@media (max-width: 480px) {
  .confirm-dialog {
    min-width: 90vw;
    padding: 22px 8vw 18px 8vw;
  }
  .btn {
    min-width: 80px;
    font-size: 0.98rem;
  }
}
</style> 