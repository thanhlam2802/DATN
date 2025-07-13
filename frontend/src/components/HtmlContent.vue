<template>
  <div class="html-content" v-html="sanitizedContent"></div>
</template>

<script>
export default {
  name: 'HtmlContent',
  props: {
    content: {
      type: String,
      default: ''
    }
  },
  computed: {
    sanitizedContent() {
      if (!this.content) return '';

      return this.content
        .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '')
        .replace(/javascript:/gi, '')
        .replace(/on\w+\s*=/gi, '');
    }
  }
}
</script>

<style scoped>
.html-content {
  line-height: 1.6;
  color: #374151;
}

.html-content :deep(h1),
.html-content :deep(h2),
.html-content :deep(h3),
.html-content :deep(h4),
.html-content :deep(h5),
.html-content :deep(h6) {
  margin-top: 1.5em;
  margin-bottom: 0.5em;
  font-weight: 600;
  color: #111827;
}

.html-content :deep(p) {
  margin-bottom: 1em;
}

.html-content :deep(ul),
.html-content :deep(ol) {
  margin-bottom: 1em;
  padding-left: 1.5em;
}

.html-content :deep(li) {
  margin-bottom: 0.25em;
}

.html-content :deep(blockquote) {
  border-left: 4px solid #e5e7eb;
  padding-left: 1em;
  margin: 1em 0;
  font-style: italic;
  color: #6b7280;
}

.html-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 1em 0;
}

.html-content :deep(th),
.html-content :deep(td) {
  border: 1px solid #d1d5db;
  padding: 0.5em;
  text-align: left;
}

.html-content :deep(th) {
  background-color: #f9fafb;
  font-weight: 600;
}

.html-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 0.5rem;
  margin: 1em 0;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
}

.html-content :deep(a) {
  color: #2563eb;
  text-decoration: underline;
}

.html-content :deep(a:hover) {
  color: #1d4ed8;
}

.html-content :deep(strong) {
  font-weight: 600;
}

.html-content :deep(em) {
  font-style: italic;
}
</style>