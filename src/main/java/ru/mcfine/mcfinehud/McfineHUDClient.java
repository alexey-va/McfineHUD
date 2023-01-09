package ru.mcfine.mcfinehud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import ru.mcfine.mcfinehud.hud.MoneyHud;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class McfineHUDClient implements ClientModInitializer {

    public static final Identifier BALANCE_ID = new Identifier(McfineHUD.MOD_ID, "balance");
    public static final Identifier INIT_ID = new Identifier(McfineHUD.MOD_ID, "init");
    public static final Identifier COMPASS_ID = new Identifier(McfineHUD.MOD_ID, "compass");
    public static final Identifier TARGET_ID = new Identifier(McfineHUD.MOD_ID, "target");
    public static final Identifier ARROW_ID = new Identifier(McfineHUD.MOD_ID, "arrow");
    private static final Identifier CANCEL_COMPASS_ID = new Identifier(McfineHUD.MOD_ID, "cancelcompass");

    public static Double playerBalance = null;
    public static Integer playerArrow = null;
    public static int playerTarget =0;
    public static Integer balanceStatus = null;
    public static int counter = 0;
    public static int counterTarget = 0;
    public static boolean targetShow = false;
    public static boolean bowInHand = false;
    public static Double compass_x=null;
    public static Double compass_y=null;
    public static Double compass_z=null;
    public static Double compass_rad=null;
    public static String compass_world=null;
    public static int counterCompass=0;
    public static int showCompass = -1;
    public static String playerWorld = null;
    public static Double playerx = null;
    public static Double playery = null;
    public static Double playerz = null;
    public static Float playeryaw = null;
    public static Double compassyaw = null;
    public static int compassId = 0;
    public static int bigCompassCountdown=-1;

    @Override
    public void onInitializeClient() {
        //System.out.println("Client init");

        ClientPlayNetworking.registerGlobalReceiver(BALANCE_ID, ((client, handler, buf, responseSender) -> {
            int readable = buf.readableBytes();
            double balance = buf.readDouble();
            if(playerBalance != null) {
                if (playerBalance > balance) {
                    balanceStatus = -1;
                    counter = 10;
                } else if (playerBalance < balance) {
                    balanceStatus = 1;
                    counter = 10;
                } else {
                    counter--;
                    if(counter == 0){
                        balanceStatus=0;
                    }
                }
            }

            if(counterTarget > 0 )counterTarget--;
            else if (counterTarget == 0) {
                targetShow = false;
            }

            if(counterCompass >0 && showCompass==1)counterCompass--;
            else if(counterCompass == 0 && showCompass==1){
                showCompass=-1;
            }

            if(bigCompassCountdown > 0)bigCompassCountdown--;
            else if(bigCompassCountdown == 0){
                showCompass=-1;
                compassyaw=null;
                compass_world=null;
                compass_y=null;
                compass_z=null;
                compass_x=null;
                bigCompassCountdown=-1;
                counterCompass=-1;
            }

            playerBalance=balance;

            StringBuilder worldName = new StringBuilder("");
            for (int i = 0; i < (readable - 8) / 2; i++) {
                worldName.append(buf.readChar());
            }
            playerWorld=worldName.toString();
        }));

        //ClientPlayNetworking.registerGlobalReceiver(ARROW_ID, ((client, handler, buf, responseSender) -> {
        //    playerArrow = buf.readInt();
        //}));

        ClientPlayNetworking.registerGlobalReceiver(COMPASS_ID, (client, handler, buf, responseSender) -> {
            int readable = buf.readableBytes();
            try {
                double x = buf.readDouble();
                double y = buf.readDouble();
                double z = buf.readDouble();
                double rad = buf.readDouble();
                StringBuilder worldName = new StringBuilder();
                for (int i = 0; i < (readable - 32) / 2; i++) {
                    worldName.append(buf.readChar());
                }
                compass_x=x;
                compass_y=y;
                compass_z=z;
                compass_rad=rad;
                compass_world=worldName.toString();
                showCompass=0;
                counterCompass=-1;
                bigCompassCountdown = 500;
            } catch (Exception ex){
                ex.printStackTrace();
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(TARGET_ID, ((client, handler, buf, responseSender) -> {
            int target = buf.readInt();
            counterTarget = 100;
            targetShow = true;
            playerTarget = target;
        }));

        ClientPlayNetworking.registerGlobalReceiver(INIT_ID, ((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                //System.out.println("Initializing connection");
                PacketByteBuf b = PacketByteBufs.create();
                b.writeBoolean(true);
                ClientPlayNetworking.send(INIT_ID, b);
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(CANCEL_COMPASS_ID, (client, handler, buf, responseSender) -> {
           compassyaw=null;
           compass_y=null;
           counterCompass=-1;
           compass_z=null;
           compass_x=null;
           compass_world=null;
           showCompass=-1;
           bigCompassCountdown=-1;
        });

        HudRenderCallback.EVENT.register(new MoneyHud());

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            boolean hasBow = false;
            if(client.player == null) return;
            ItemStack itemStack = client.player.getMainHandStack();
            if(itemStack.isOf(Items.BOW) || itemStack.isOf(Items.CROSSBOW)){
                hasBow = true;
            }
            itemStack = client.player.getOffHandStack();
            if(itemStack.isOf(Items.BOW) || itemStack.isOf(Items.CROSSBOW)){
                hasBow = true;
            }


            int arrows = 0;
            arrows+=client.player.getInventory().count(Items.ARROW);
            arrows+=client.player.getInventory().count(Items.SPECTRAL_ARROW);
            arrows+=client.player.getInventory().count(Items.TIPPED_ARROW);
            playerArrow=arrows;
            bowInHand = hasBow;

            playerx = client.player.getX();
            playery = client.player.getY();
            playerz = client.player.getZ();
            playeryaw = (client.player.getYaw()%360 + 360)%360;
            if(playeryaw>180.0) playeryaw = -180f+(playeryaw-180f);

            if(compass_x == null || compass_y == null || compass_z == null || compass_world == null) return;
            else{
                compassyaw = ((compass_z-playerz))
                        /(Math.sqrt((compass_x-playerx)*(compass_x-playerx) + (compass_z-playerz)*(compass_z-playerz)));
                compassyaw = (Math.toDegrees(Math.acos(compassyaw)));
                if((compass_x-playerx) > 0) compassyaw=-compassyaw;
                //System.out.println("compass yaw: "+compassyaw+" | player yaw: "+playeryaw+" | yaw:"+(compassyaw-playeryaw));

                double directionYaw = compassyaw-playeryaw;
                if(directionYaw< -180.0)directionYaw = 360+directionYaw;

                int direction = (int)Math.round(directionYaw/360f*31.5 )+8-24;
                if(direction<0) direction = 32 + direction;
                compassId=direction;
                //System.out.println("Compass id: "+direction);

                double distance = Math.sqrt((compass_x-playerx)*(compass_x-playerx)+
                        (compass_y-playery)*(compass_y-playery) + (compass_z-playerz)*(compass_z-playerz));

                if(distance<= compass_rad && playerWorld.equals(compass_world)){
                    counterCompass = 50;
                    showCompass=1;
                    //System.out.println("Approached");

                    compass_x = null;
                    compass_y = null;
                    compass_z=null;
                    compass_world=null;
                }
            }
        });

    }




}
