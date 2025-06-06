<template>
  <div>
    <div v-for="item in notificationSections" :key="item.title">
      <div
          class="flex flex-col p-5 rounded-xl bg-white border border-gray-200 shadow-sm hover:shadow-md transition mb-5">
        <h2 class="font-semibold text-gray-800 mb-4">{{ item.title }}</h2>
        <div class="flex flex-col gap-2">
          <div
              class="flex items-center justify-between"
              v-for="option in item.options"
              :key="option.title"
          >
            <p>{{ option.title }}</p>
            <label class="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" class="sr-only peer" v-model="option.enable" />
              <div
                  class="w-11 h-6 bg-gray-300 rounded-full peer peer-checked:bg-green-600 transition-colors duration-300">
              </div>
              <div
                  class="absolute left-0.5 top-0.5 w-5 h-5 bg-white rounded-full shadow transform peer-checked:translate-x-full transition duration-300">
              </div>
            </label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "EmailNotificationSetting",
  data() {
    return {
      notificationSections: [],
    };
  },
  created() {
    this.initializeSections();
  },
  watch: {
    // Reinitialize sections when locale changes
    '$i18n.locale'() {
      this.initializeSections();
    }
  },
  methods: {
    initializeSections() {
      console.log(this.$t)
      this.notificationSections = [
        {
          title: this.$t("notifications.promotion"),
          options: [
            {
              title: this.$t("notifications.priceAlert"),
              enable: false,
              id: 1,
            },
            {
              title: this.$t("notifications.promoAlert"),
              enable: true,
              id: 2,
            },
          ],
        },
        {
          title: this.$t("notifications.feedback"),
          options: [
            {
              title: this.$t("notifications.reviewReminder"),
              enable: false,
              id: 1,
            },
            {
              title: this.$t("notifications.survey"),
              enable: true,
              id: 2,
            },
          ],
        },
        {
          title: this.$t("notifications.accountBooking"),
          options: [
            {
              title: this.$t("notifications.activityLog"),
              enable: true,
              id: 1,
            },
            {
              title: this.$t("notifications.booking"),
              enable: true,
              id: 2,
            },
            {
              title: this.$t("notifications.rewards"),
              enable: true,
              id: 3,
            },
          ],
        },
      ];
    },
  },
};
</script>
