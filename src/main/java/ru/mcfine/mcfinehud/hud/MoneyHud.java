package ru.mcfine.mcfinehud.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import ru.mcfine.mcfinehud.McfineHUD;
import ru.mcfine.mcfinehud.McfineHUDClient;

public class MoneyHud implements HudRenderCallback {

    private static final Identifier CHAR_0 = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/digit_0.png");
    private static final Identifier CHAR_1 = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/digit_1.png");
    private static final Identifier CHAR_2 = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/digit_2.png");
    private static final Identifier CHAR_3 = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/digit_3.png");
    private static final Identifier CHAR_4 = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/digit_4.png");
    private static final Identifier CHAR_5 = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/digit_5.png");
    private static final Identifier CHAR_6 = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/digit_6.png");
    private static final Identifier CHAR_7 = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/digit_7.png");
    private static final Identifier CHAR_8 = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/digit_8.png");
    private static final Identifier CHAR_9 = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/digit_9.png");
    private static final Identifier CHAR_K = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/char_k.png");
    private static final Identifier CHAR_M = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/char_m.png");
    private static final Identifier CHAR_B = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/char_b.png");
    private static final Identifier CHAR_DOT = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/char_dot.png");
    private static final Identifier CHAR_COIN = new Identifier(McfineHUD.MOD_ID,
            "money/money_icon.png");
    private static final Identifier CHAR_ARROW_UP = new Identifier(McfineHUD.MOD_ID,
            "money/char_arrow_up.png");
    private static final Identifier CHAR_ARROW_DOWN = new Identifier(McfineHUD.MOD_ID,
            "money/char_arrow_down.png");


    private static final Identifier QUIVER = new Identifier(McfineHUD.MOD_ID,
            "quiver/quiver.png");
    private static final Identifier QUIVER_EMPTY = new Identifier(McfineHUD.MOD_ID,
            "quiver/quiver_empty.png");
    private static final Identifier QUIVER_HALF = new Identifier(McfineHUD.MOD_ID,
            "quiver/quiver_half.png");


    private static final Identifier TARGET = new Identifier(McfineHUD.MOD_ID,
            "arrow_target/arrow_target.png");
    private static final Identifier PERCENT = new Identifier(McfineHUD.MOD_ID,
            "generic_chars/char_percentage.png");


    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        int x=0, y=0;
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc != null){
            int width = mc.getWindow().getScaledWidth();
            int height = mc.getWindow().getScaledHeight();

            x=width/2;
            y=height;
        }

        String balance = getBalanceString();

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int ifDot = 0;
        if(balance.length()>0){
            RenderSystem.setShaderTexture(0, CHAR_COIN);
            DrawableHelper.drawTexture(matrixStack, x+79, y-61, 0, 0, 11, 11, 11, 11);
        }
        for(int i=0;i<balance.length();i++){
            String s = String.valueOf(balance.charAt(balance.length() - 1 - i));
            RenderSystem.setShaderTexture(0, getIdentifier(s));
            int w = 6;
            int h = 8;
            if (".".equals(s)) {
                w = 2;
                ifDot =1;
                DrawableHelper.drawTexture(matrixStack, x+74-6*i + ifDot*4, y-59, 0, 0, w, h, w, h);
                continue;
            }

            DrawableHelper.drawTexture(matrixStack, x+74-6*i + ifDot*4, y-59, 0, 0, w, h, w, h);
        }
        if(McfineHUDClient.balanceStatus != null) {
            int x1 = x + 74 - 6 * balance.length() + ifDot * 4-3;
            if (McfineHUDClient.balanceStatus == 1) {
                RenderSystem.setShaderTexture(0, CHAR_ARROW_UP);
                DrawableHelper.drawTexture(matrixStack, x1, y - 61, 0, 0, 8, 11, 8, 11);
            } else if (McfineHUDClient.balanceStatus == -1) {
                RenderSystem.setShaderTexture(0, CHAR_ARROW_DOWN);
                DrawableHelper.drawTexture(matrixStack, x1, y - 61, 0, 0, 8, 11, 8, 11);
            }
        }




        // Bow hud
        if(McfineHUDClient.bowInHand){
            Identifier id = QUIVER_EMPTY;
            if(McfineHUDClient.playerArrow > 0 && McfineHUDClient.playerArrow < 64){
                id = QUIVER_HALF;
            } else if(McfineHUDClient.playerArrow >= 64){
                id = QUIVER;
            }
            RenderSystem.setShaderTexture(0, id);
            int shift = 0;
            try {
                if (!MinecraftClient.getInstance().player.getOffHandStack().isOf(Items.AIR)) shift=-28;
            } catch (Exception ex){
                ex.printStackTrace();
            }
            DrawableHelper.drawTexture(matrixStack, x - 119+shift, y - 20, 0, 0, 18, 18, 18, 18);

            String arrows = McfineHUDClient.playerArrow+"";

            for(int i=0;i<arrows.length();i++){
                String s = String.valueOf(arrows.charAt(arrows.length() - 1 - i));
                RenderSystem.setShaderTexture(0, getIdentifier(s));
                int w = 7;
                int h = 9;
                DrawableHelper.drawTexture(matrixStack, x-127-w*i+shift, y-16, 0, 0, w, h, w, h);
            }
        }


        // target hud
        if(McfineHUDClient.targetShow){
            RenderSystem.setShaderTexture(0, TARGET);
            DrawableHelper.drawTexture(matrixStack, x - 118, y - 46, 0, 0, 18, 18, 18, 18);

            int score = (int)((McfineHUDClient.playerTarget/15.0)*100);
            String arrows = score+"%";

            for(int i=0;i<arrows.length();i++){
                String s = String.valueOf(arrows.charAt(arrows.length() - 1 - i));
                RenderSystem.setShaderTexture(0, getIdentifier(s));
                int w = 7;
                int h = 9;
                DrawableHelper.drawTexture(matrixStack, x-128-w*i, y-42, 0, 0, w, h, w, h);
            }
        }

        // compass hud
        if(McfineHUDClient.showCompass == 0){
            if(!McfineHUDClient.compass_world.equals(McfineHUDClient.playerWorld)) return;
            String id = "";
            if(McfineHUDClient.compassId < 10){
                id+="0"+McfineHUDClient.compassId;
            } else{
                id+=McfineHUDClient.compassId;
            }
            //System.out.println("ID: "+McfineHUDClient.compassId);
            Identifier identifier = new Identifier(McfineHUD.MOD_ID, "compass/compass_"+id+".png");
            RenderSystem.setShaderTexture(0, identifier);
            DrawableHelper.drawTexture(matrixStack, x-16, y - 80,0,0,32,32,32,32);
        } else if(McfineHUDClient.showCompass == 1){
            Identifier identifier = new Identifier(McfineHUD.MOD_ID, "compass/compass_reached.png");
            RenderSystem.setShaderTexture(0, identifier);
            DrawableHelper.drawTexture(matrixStack, x-16, y - 80,0,0,32,32,32,32);
        }

    }

    private String getBalanceString(){
        if(McfineHUDClient.playerBalance == null) return "";
        double bal = McfineHUDClient.playerBalance;
        String result="";
        if (bal>5000){
            if(bal>1000000000){
                result+=(int)bal/1000000000+"."+(int)((bal/1000000000)%1*100)+"b";
            }
            else if(bal > 1000000){
                result+=(int)bal/1000000+"."+(int)((bal/1000000)%1*100)+"m";
            } else{
                result+=(int)bal/1000+"."+(int)((bal/1000)%1*100)+"k";
            }
        } else {
            result+=(int)bal+"."+(int)((bal%1)*10);
        }
        return result;
    }


    private Identifier getIdentifier(String ch){
        switch (ch){
            case "0" -> {
                return CHAR_0;
            }
            case "1" -> {
                return CHAR_1;
            }
            case "2" -> {
                return CHAR_2;
            }
            case "3" -> {
                return CHAR_3;
            }
            case "4" -> {
                return CHAR_4;
            }
            case "5" -> {
                return CHAR_5;
            }
            case "6" -> {
                return CHAR_6;
            }
            case "7" -> {
                return CHAR_7;
            }
            case "8" -> {
                return CHAR_8;
            }
            case "9" -> {
                return CHAR_9;
            }
            case "k" ->{
                return CHAR_K;
            }
            case "b" ->{
                return CHAR_B;
            }
            case "m" ->{
                return CHAR_M;
            }
            case "." ->{
                return CHAR_DOT;
            }
            case "%" ->{
                return PERCENT;
            }
        }
        return CHAR_0;
    }
}
