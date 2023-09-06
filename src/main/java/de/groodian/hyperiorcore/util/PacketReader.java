package de.groodian.hyperiorcore.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import java.util.NoSuchElementException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public abstract class PacketReader {

    private static final String CHANNEL_HANDLER_NAME = "Hyperior_";

    protected final Player player;

    private final String channelHandlerName;
    private final Channel channel;

    public PacketReader(String channelHandlerName, Player player) {
        this.player = player;
        this.channelHandlerName = CHANNEL_HANDLER_NAME + channelHandlerName;

        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        this.channel = connection.connection.channel;
    }

    protected abstract void readPacket(Packet<?> packet);

    public void inject() {
        uninject();

        try {
            channel.pipeline().addAfter("decoder", channelHandlerName, new MessageToMessageDecoder<Packet<?>>() {
                @Override
                protected void decode(ChannelHandlerContext ctx, Packet<?> packet, List<Object> out) {
                    out.add(packet);
                    readPacket(packet);
                }
            });
        } catch (NoSuchElementException e) {
            // ignore, only happens if player immediately disconnects after join
        }

    }

    public void uninject() {
        if (channel.pipeline().get(channelHandlerName) != null) {
            channel.pipeline().remove(channelHandlerName);
        }
    }
}
