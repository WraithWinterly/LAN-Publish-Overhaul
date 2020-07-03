package com.IsRare.PublishOverhaul.mixins;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.util.NetworkUtils;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.PublishCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

@Mixin(PublishCommand.class)
public class PublishCommandMixin {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.publish.failed"));
    private static final DynamicCommandExceptionType ALREADY_PUBLISHED_EXCEPTION = new DynamicCommandExceptionType((object) -> {
       return new TranslatableText("commands.publish.alreadyPublished", new Object[]{object});
    });

    @Overwrite
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("open").requires((serverCommandSource) -> {
           return true;
        })).executes((commandContext) -> {
           return execute((ServerCommandSource)commandContext.getSource(), NetworkUtils.findLocalPort());
        })).then(CommandManager.argument("port", IntegerArgumentType.integer(0, 65535)).executes((commandContext) -> {
           return execute((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "port"));
        })));
     }

     @Overwrite
     public static int execute(ServerCommandSource source, int port) throws CommandSyntaxException {
        if (source.getMinecraftServer().isRemote()) {
           throw ALREADY_PUBLISHED_EXCEPTION.create(source.getMinecraftServer().getServerPort());
        } else if (!source.getMinecraftServer().openToLan(source.getMinecraftServer().getDefaultGameMode(), false, port)) {
           throw FAILED_EXCEPTION.create();
        } else {
           source.sendFeedback(new TranslatableText("commands.publish.success", new Object[]{port}), true);
           return port;
        }
     }
    
}