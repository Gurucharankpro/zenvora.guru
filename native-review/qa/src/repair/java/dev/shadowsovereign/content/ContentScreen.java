package dev.shadowsovereign.content;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Scrollable and touch-draggable; owner state is accepted only from the server. */
public final class ContentScreen extends Screen {
 private double scroll;private int left,top,panelWidth,viewport,lastFlags=-1;private boolean dragging;
 private static final String[] KEYS={"enabled","building","combat","performance","follow","regroup","quality","fog"};
 private static final String[] ICONS={"recovery","building_mode","combat_assist","performance_mode","smart_follow","regroup","performance_mode","recovery"};
 ContentScreen(){super(Component.translatable("content.shadowsovereign.title"));}
 @Override protected void init(){panelWidth=Math.max(180,Math.min(320,width-16));left=(width-panelWidth)/2;top=30;viewport=Math.max(60,height-65);build();}
 private void build(){
  clearWidgets();int[] actions={6,1,2,3,4,5};int[] masks={1,2,4,16,8,0};
  for(int i=0;i<KEYS.length;i++){
   final int row=i;int y=top+8+i*36-(int)scroll;
   String suffix=i<5?Component.translatable((ContentClient.STATUS.flags()&masks[i])!=0?"options.on":"options.off").getString():i==6?Component.translatable("content.shadowsovereign.quality."+ContentConfig.QUALITY.get().name().toLowerCase(java.util.Locale.ROOT)).getString():i==7?Component.translatable(ContentConfig.FOG.get()?"options.on":"options.off").getString():"";
   Button button=Button.builder(Component.translatable("content.shadowsovereign."+KEYS[i]).append(suffix.isEmpty()?"":" : "+suffix),b->{
    if(row<6)ContentNetwork.action(actions[row]);
    else if(row==6){var q=ContentConfig.QUALITY.get();ContentConfig.QUALITY.set(ContentConfig.Quality.values()[(q.ordinal()+1)%3]);ContentConfig.CLIENT.save();build();}
    else {ContentConfig.FOG.set(!ContentConfig.FOG.get());ContentConfig.CLIENT.save();build();}
   }).bounds(left+38,y,panelWidth-52,26).build();
   button.visible=y>=top&&y+26<=top+viewport;
   button.active=row>=6||minecraft.player!=null&&ContentConfig.flag("general.enabled")&&(row!=5||(ContentClient.STATUS.flags()&1)!=0);
   addRenderableWidget(button);
  }
 }
 @Override public void tick(){super.tick();if(lastFlags!=ContentClient.STATUS.flags()){lastFlags=ContentClient.STATUS.flags();build();}}
 @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partial){
  renderBackground(g);g.fill(left,top,left+panelWidth,top+viewport,0xec171022);
  g.drawCenteredString(font,title,width/2,10,0xefdbff);
  g.enableScissor(left,top,left+panelWidth,top+viewport);
  for(int i=0;i<KEYS.length;i++){
   int y=top+8+i*36-(int)scroll;
   if(y+26>top&&y<top+viewport)g.blit(new ResourceLocation("shadowsovereign","textures/gui/x4/icons_32/icon_"+ICONS[i]+".png"),left+6,y,26,26,0,0,32,32,32,32);
  }
  super.render(g,mouseX,mouseY,partial);g.disableScissor();
  int content=KEYS.length*36+16;
  if(content>viewport){int thumb=Math.max(12,viewport*viewport/content);int offset=(int)(scroll/Math.max(1,content-viewport)*(viewport-thumb));g.fill(left+panelWidth-5,top+offset,left+panelWidth-2,top+offset+thumb,0xffb887e6);}
  g.drawCenteredString(font,Component.translatable("content.shadowsovereign.drag"),width/2,height-19,0xbda7cc);
 }
 private void scroll(double delta){scroll=Math.max(0,Math.min(Math.max(0,KEYS.length*36+16-viewport),scroll+delta));build();}
 @Override public boolean mouseScrolled(double x,double y,double amount){scroll(-amount*24);return true;}
 @Override public boolean mouseClicked(double x,double y,int button){if(super.mouseClicked(x,y,button))return true;if(button==0&&x>=left&&x<=left+panelWidth&&y>=top&&y<=top+viewport){dragging=true;return true;}return false;}
 @Override public boolean mouseDragged(double x,double y,int button,double dx,double dy){if(dragging&&button==0){scroll(-dy);return true;}return super.mouseDragged(x,y,button,dx,dy);}
 @Override public boolean mouseReleased(double x,double y,int button){dragging=false;return super.mouseReleased(x,y,button);}
 @Override public boolean isPauseScreen(){return false;}
}
