import type { Plugin } from "@opencode-ai/plugin";
import { mkdir, writeFile } from "node:fs/promises";
import { join } from "node:path";

const TARGET = ["_temp", "session-summary.md"];

export default (async ({ client, directory }) => {
  return {
    async event(input) {
      const ev = input.event;
      if (!ev || ev.type !== "session.compacted") return;
      const sessionID = ev.properties?.sessionID;
      if (!sessionID) return;

      try {
        // Ambil pesan sesi, cari teks summary hasil kompaksi (part
        // "compaction" pada pesan terbaru, teks pendampingnya).
        const res: any = await client.session.messages({ sessionID });
        const list: any[] = res?.data ?? res?.body ?? [];

        let summary = "";
        for (let i = list.length - 1; i >= 0; i--) {
          const parts: any[] = list[i]?.parts ?? [];
          if (!parts.some((p) => p?.type === "compaction")) continue;
          const text = parts
            .filter(
              (p) => p?.type === "text" && typeof p.text === "string" && !p.ignored
            )
            .map((p) => p.text)
            .join("\n");
          if (text.trim()) {
            summary = text;
            break;
          }
        }
        if (!summary.trim()) return;

        const dir = join(directory, TARGET[0]);
        await mkdir(dir, { recursive: true });
        await writeFile(join(directory, ...TARGET), summary.trim() + "\n", "utf8");
      } catch (err) {
        console.error("[summary-to-file]", err);
      }
    },
  };
}) satisfies Plugin;