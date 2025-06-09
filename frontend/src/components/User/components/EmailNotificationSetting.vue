<template>
   <div>
      <div v-for="item in notificationSections" :key="item.title">
         <div
            class="flex flex-col p-5 rounded-xl bg-white border border-gray-200 shadow-sm hover:shadow-md transition mb-5"
         >
            <h1 class="text-xl font-semibold text-gray-800 mb-4">
               {{ item.title }}
            </h1>
            <div class="flex flex-col gap-2">
               <div
                  class="flex items-center justify-between mb-4"
                  v-for="option in item.options"
                  :key="option.title"
               >
                  <div>
                     <p class="font-bold text-gray-800 mb-2">
                        {{ option.title }}
                     </p>
                     <p>{{ option.description }}</p>
                  </div>

                  <label
                     class="relative inline-flex items-center cursor-pointer"
                  >
                     <input
                        type="checkbox"
                        class="sr-only peer"
                        v-model="option.enable"
                     />
                     <div
                        class="w-11 h-6 bg-gray-300 rounded-full peer peer-checked:bg-green-600 transition-colors duration-300"
                     ></div>
                     <div
                        class="absolute left-0.5 top-0.5 w-5 h-5 bg-white rounded-full shadow transform peer-checked:translate-x-full transition duration-300"
                     ></div>
                  </label>
               </div>
            </div>
         </div>
      </div>
   </div>
</template>

<script>
import { useI18n } from 'vue-i18n';

export default {
   name: 'EmailNotificationSetting',
   data() {
      return {
         notificationSections: [],
      };
   },
   mounted() {
      const { messages, locale } = useI18n();

      const settings =
         messages.value[locale.value].notificationSettings['EMAIL'];

      for (const data of settings) {
         this.notificationSections.push({
            title: data['title'],
            options: data['options'],
         });
      }
   },
};
</script>
