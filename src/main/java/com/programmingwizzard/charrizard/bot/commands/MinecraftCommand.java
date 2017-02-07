package com.programmingwizzard.charrizard.bot.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.programmingwizzard.charrizard.bot.commands.basic.CMessage;
import com.programmingwizzard.charrizard.bot.commands.basic.Command;
import com.programmingwizzard.charrizard.bot.response.skript.SkriptServerResponse;
import com.programmingwizzard.charrizard.bot.response.skript.SkriptServerResponses;
import com.programmingwizzard.charrizard.utils.BooleanUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.awt.*;

/*
 * @author ProgrammingWizzard
 * @date 06.02.2017
 */
public class MinecraftCommand extends Command
{
    private final SkriptServerResponses responses;

    public MinecraftCommand()
    {
        super("minecraft");
        this.responses = new SkriptServerResponses();
    }

    @Override
    public void handle(CMessage message, String[] args) throws RateLimitedException
    {
        TextChannel channel = message.getChannel();
        if (args.length == 0 || args.length == 1)
        {
            sendUsage(message, "!minecraft <status>");
            return;
        }
        switch (args[1])
        {
            case "status":
                this.checkStatus(message, args);
                break;
            default:
                sendUsage(message, "!minecraft <status>");
                break;
        }
    }

    private void checkStatus(CMessage message, String[] args)
    {
        TextChannel channel = message.getChannel();
        if (args.length != 3)
        {
            sendUsage(message, "!minecraft status <ip>");
            return;
        }
        String server = args[2];
        if (server == null || server.isEmpty())
        {
            sendUsage(message, "!minecraft status <ip>");
            return;
        }

        responses.call(server, response -> {
            if (response == null) {
                sendError(message, "An error occurred while connecting with api.skript.pl");
                return;
            }

            String info;
            if (response.isOnline()) {
                StringBuilder list = new StringBuilder();
                for (String player : response.getPlayersList()) {
                    list.append(", ").append(player);
                }
                info =
                    "Online: yes\n" +
                    String.format("Latency: %.2fms\n", response.getLatency()) +
                    String.format("Version: %s (Protocol #%d)\n", response.getVersion(), response.getProtocol()) +
                    String.format("Description:\n %s\n", response.getDescription()) +
                    String.format("Players (%d/%d): %s", response.getOnlinePlayers(), response.getMaxPlayers(), list.substring(2));
            } else {
                info = "Online: no";
            }

            EmbedBuilder builder = getEmbedBuilder()
                    .setTitle("Charrizard")
                    .setFooter("© 2017 Charrizard contributors", null)
                    .setUrl("https://github.com/ProgrammingWizzard/Charrizard/")
                    .setColor(new Color(0, 250, 0))
                    .addField(":information_source: " + server, info, true);
            sendEmbedMessage(message, builder);
        });
    }
}
