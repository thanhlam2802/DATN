<template>
  <div>
    <textarea ref="editorRef"></textarea>
  </div>
</template>

<script>
import { onMounted, ref, onBeforeUnmount, watch } from "vue";

export default {
  name: "Ckeditor",
  props: {
    modelValue: String,
    disabled: Boolean,
  },
  emits: ["update:modelValue"],
  setup(props, { emit }) {
    const editorRef = ref(null);
    let editorInstance = null;

    const initEditor = () => {
      window.ClassicEditor.create(editorRef.value, {
        initialData: props.modelValue || "",
      })
        .then((editor) => {
          editorInstance = editor;

          // Cập nhật dữ liệu mỗi khi nội dung thay đổi
          editor.model.document.on("change:data", () => {
            emit("update:modelValue", editor.getData());
          });

          // Disable nếu cần
          if (props.disabled) {
            editor.enableReadOnlyMode("manual-disable");
          }
        })
        .catch((error) => {
          console.error("CKEditor init error:", error);
        });
    };

    onMounted(initEditor);

    onBeforeUnmount(() => {
      if (editorInstance) {
        editorInstance.destroy().catch((error) => console.error(error));
      }
    });

    // Watch để cập nhật khi prop `disabled` thay đổi
    watch(
      () => props.disabled,
      (newVal) => {
        if (editorInstance) {
          if (newVal) {
            editorInstance.enableReadOnlyMode("manual-disable");
          } else {
            editorInstance.disableReadOnlyMode("manual-disable");
          }
        }
      }
    );

    return {
      editorRef,
    };
  },
};
</script>
