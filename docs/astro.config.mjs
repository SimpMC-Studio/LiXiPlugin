import { defineConfig } from "astro/config";
import starlight from "@astrojs/starlight";

export default defineConfig({
  site: "https://simpmc-studio.github.io/LiXiPlugin",
  integrations: [
    starlight({
      title: "LiXiPlugin",
      description:
        "Vietnamese Lucky Money Distribution System for Paper/Folia Minecraft Servers",
      social: {
        github: "https://github.com/SimpMC-Studio/LiXiPlugin",
      },

      defaultLocale: "root",
      locales: {
        en: {
          label: "English",
          lang: "en",
        },
        root: {
          label: "Tiếng Việt",
          lang: "vi",
        },
      },
      customCss: ["./src/styles/custom.css"],
      sidebar: [
        {
          label: "Introduction",
          translations: { vi: "Giới thiệu" },
          items: [
            {
              slug: "getting-started",
              label: "Getting Started",
              translations: { vi: "Bắt đầu" },
            },
          ],
        },
        {
          label: "Configuration",
          translations: { vi: "Cấu hình" },
          items: [
            {
              slug: "configuration/main-config",
              label: "Main Config",
              translations: { vi: "Cấu hình chính" },
            },
          ],
        },
        {
          label: "Commands",
          translations: { vi: "Lệnh" },
          items: [
            {
              slug: "commands/user-commands",
              label: "User Commands",
              translations: { vi: "Lệnh người chơi" },
            },
            {
              slug: "commands/admin-commands",
              label: "Admin Commands",
              translations: { vi: "Lệnh quản trị" },
            },
          ],
        },
        {
          label: "Guides",
          translations: { vi: "Hướng dẫn" },
          items: [
            {
              slug: "guides/set-item",
              label: "Setting Envelope Item",
              translations: { vi: "Thiết lập vật phẩm" },
            },
            {
              slug: "guides/permissions",
              label: "Permissions",
              translations: { vi: "Quyền hạn" },
            },
          ],
        },
      ],
    }),
  ],
});

