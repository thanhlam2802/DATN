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
        toolbar: {
          items: [
            'heading', '|',
            'bold', 'italic', 'underline', 'strikethrough', 'code', '|',
            'fontFamily', 'fontSize', 'fontColor', 'fontBackgroundColor', 'highlight', '|',
            'alignment', '|',
            'bulletedList', 'numberedList', 'outdent', 'indent', '|',
            'link', 'blockQuote', 'insertTable', 'imageUpload', 'mediaEmbed', '|',
            'undo', 'redo', 'removeFormat', 'horizontalLine', 'specialCharacters', 'subscript', 'superscript',
          ]
        },
        image: {
          upload: {
            types: ['jpeg', 'png', 'gif', 'webp']
          }
        },
        alignment: {
          options: [ 'left', 'center', 'right', 'justify' ]
        },
        fontFamily: {
          options: [
            'default',
            'Arial, Helvetica, sans-serif',
            'Courier New, Courier, monospace',
            'Georgia, serif',
            'Lucida Sans Unicode, Lucida Grande, sans-serif',
            'Tahoma, Geneva, sans-serif',
            'Times New Roman, Times, serif',
            'Trebuchet MS, Helvetica, sans-serif',
            'Verdana, Geneva, sans-serif'
          ]
        },
        fontSize: {
          options: [ 8, 10, 12, 14, 'default', 18, 24, 36, 48 ]
        },
        fontColor: {
          columns: 8,
          documentColors: 12
        },
        fontBackgroundColor: {
          columns: 8,
          documentColors: 12
        },
        highlight: {
          options: [
            { model: 'yellowMarker', class: 'marker-yellow', title: 'Yellow marker', color: 'var(--ck-highlight-marker-yellow)', type: 'marker' },
            { model: 'greenMarker', class: 'marker-green', title: 'Green marker', color: 'var(--ck-highlight-marker-green)', type: 'marker' },
            { model: 'pinkMarker', class: 'marker-pink', title: 'Pink marker', color: 'var(--ck-highlight-marker-pink)', type: 'marker' },
            { model: 'blueMarker', class: 'marker-blue', title: 'Blue marker', color: 'var(--ck-highlight-marker-blue)', type: 'marker' },
            { model: 'redPen', class: 'pen-red', title: 'Red pen', color: 'var(--ck-highlight-pen-red)', type: 'pen' },
            { model: 'greenPen', class: 'pen-green', title: 'Green pen', color: 'var(--ck-highlight-pen-green)', type: 'pen' }
          ]
        },
      })
        .then((editor) => {
          editorInstance = editor;

          // Custom upload adapter for image upload
          editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
            return {
              upload: () => {
                return loader.file
                  .then(file => {
                    console.log('Uploading image:', file.name, 'Size:', file.size);
                    
                    const data = new FormData();
                    data.append('file', file);
                    
                    return fetch('/api/upload', {
                      method: 'POST',
                      body: data,
                    })
                      .then(response => {
                        if (!response.ok) {
                          throw new Error(`HTTP error! status: ${response.status}`);
                        }
                        return response.json();
                      })
                      .then(result => {
                        console.log('Upload result:', result);
                        
                        if (result.error) {
                          throw new Error(result.error);
                        }
                        
                        if (!result.url) {
                          throw new Error('No URL returned from server');
                        }
                        
                        // CKEditor expects { default: "image_url" }
                        return { default: result.url };
                      })
                      .catch(error => {
                        console.error('Upload failed:', error);
                        // Show error to user
                        if (window.$toast) {
                          window.$toast('Lỗi upload ảnh: ' + error.message, 'error');
                        }
                        throw error;
                      });
                  });
              },
              abort: () => {
                console.log('Upload aborted');
              }
            };
          };

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
