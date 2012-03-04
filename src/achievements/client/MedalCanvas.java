package achievements.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import achievements.shared.Medal;
import achievements.shared.Veteran;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;

import java.util.Set;

public class MedalCanvas extends Composite{

  private Canvas canvas;
  String selected;
  private HashMap<Medal, int[]> bounds = new HashMap<Medal, int[]>();
  Map<Medal, ImageElement> images;
  private ImageElement star = (ImageElement)new Image("star.png").getElement().cast();
  private HashMap<String, Veteran> veterans;
  Medal hoveredMedal = null;

  MedalCanvas(HashMap<String, Veteran> veterans){

    this.veterans = veterans;

    canvas = Canvas.createIfSupported();

    initWidget(canvas);
  }

  void setSelected(String selected){
    this.selected = selected;
    paint();
  }

  Medal getHovered(int x, int y){

    if(selected==null){
      return null;
    }

    for(Entry<Medal, int[]> e : bounds.entrySet()){
      int[] b = e.getValue();
      if(x>=b[0] && x<b[0]+b[2] && y>=b[1] && y<b[1]+b[3]){
        return e.getKey();
      }
    }

    return null;
  }

  void resize(int canvasWidth, int minCanvasHeight){

    int numColumns = canvasWidth/(130);
    int row = 0;
    int column = 0;

    if(selected==null){
      return;
    }

    bounds.clear();

    for(Medal m : veterans.get(selected).awards.keySet()){
      bounds.put(m, new int[]{column*130, row*170, 130, 170});

      column++;
      if(column>=numColumns){
        column = 0;
        row++;
      }
    }

    int canvasHeight = Math.max(minCanvasHeight, (row+(column==0 ? 0 : 1))*170);

    canvas.setPixelSize(canvasWidth, canvasHeight);
    canvas.setCoordinateSpaceWidth(canvasWidth);
    canvas.setCoordinateSpaceHeight(canvasHeight);

    paint();
  }

  void paint(){

    Context2d context = canvas.getContext2d();

    context.setFillStyle("#008000");
    context.fillRect(0, 0, DOM.getIntStyleAttribute(getElement(), "width"),
        DOM.getIntStyleAttribute(getElement(), "height"));

    if(selected==null){
      return;
    }

    Veteran v = veterans.get(selected);
    // draw every medal for the selected veteran
    for(Entry<Medal, Set<String>> award : v.awards.entrySet()){
      Medal m = award.getKey();
      int[] b = bounds.get(m);
      drawMedal(context, m, award.getValue().size(), b[0]+65, b[1]+100);
    }
  }
  
  /* This is REALLY inefficient.  The entire canvas is being repainted every time, instead of only the medals we care about.
   * Additionally, the static pieces of a medal (most of it) really ought to be rendered to a backbuffer once, then reused.
   * 
   * But performance seems fine, even on my phone.  So fuck it.
   */
  private void drawMedal(Context2d context, Medal medal, int starCount, int x, int y){

    context.setShadowOffsetX(5);
    context.setShadowOffsetY(5);

    context.setShadowColor("#004000");

    context.beginPath();
    context.setFillStyle("#440000");

    context.lineTo(x-60, y-100);
    context.lineTo(x-60, y-60);
    context.lineTo(x-30, y-40);
    context.lineTo(x+30, y-40);
    context.lineTo(x+60, y-60);
    context.lineTo(x+60, y-100);
    context.fill();

    context.setShadowOffsetX(0);
    context.setShadowOffsetY(0);

    context.beginPath();

    CanvasGradient g = context.createLinearGradient(0, y-100, 0, y-40);
    g.addColorStop(0, "#550000");
    g.addColorStop(1, "#aa0000");
    context.setFillStyle(g);

    context.lineTo(x-60+2, y-100+2);
    context.lineTo(x-60+2, y-60-2);
    context.lineTo(x-30+2, y-40-2);
    context.lineTo(x+30-2, y-40-2);
    context.lineTo(x+60-2, y-60-2);
    context.lineTo(x+60-2, y-100+2);
    context.fill();

    context.setFillStyle("#550000");
    for(int i=-57; i<0; i+=4){
      context.fillRect(x+i, y-98, 2, 76+i/1.5);
      context.fillRect(x+i+56, y-98, 2, 37-i/1.5);
    }

    context.setShadowOffsetX(5);
    context.setShadowOffsetY(5);

    context.beginPath();
    context.setFillStyle("#444444");
    context.arc(x, y, 60, 0, 2*Math.PI);
    context.fill();

    context.setShadowOffsetX(0);
    context.setShadowOffsetY(0);

    context.beginPath();
    context.setFillStyle("#ffca3b");

    context.arc(x, y, 58, 0, 2*Math.PI);
    context.fill();

    context.beginPath();
    context.setFillStyle("#444444");

    context.arc(x, y, 56, 0, 2*Math.PI);
    context.fill();

    // If the image isn't yet loaded, ignore it.  It'll be along eventually.
    if(images.get(medal)!=null){
      context.drawImage(images.get(medal), x-50, y-50);
    }

    g = context.createRadialGradient(x, y, 30, x, y, 60);
    g.addColorStop(0, "rgba(0, 0, 0, 0)");
    g.addColorStop(1, "rgba(0, 0, 0, 0.75)");
    context.setFillStyle(g);
    context.arc(x, y, 56, 0, 2*Math.PI);
    context.fill();

    if(hoveredMedal!=null && medal!=hoveredMedal){
      context.beginPath();
      context.setFillStyle("rgba(0, 0, 0, 0.5)");
      context.arc(x, y, 56, 0, 2*Math.PI);
      context.fill();
    }

    if(selected.equals(Achievements.ALL_MEDALS)){
      context.setFillStyle("#ffca3b");      
      context.setTextAlign(TextAlign.CENTER);
      context.setFont("bold 30px sans-serif");
      int count = 0;
      for(Veteran v : veterans.values()){
        if(!v.shortName.equals(Achievements.ALL_MEDALS) && v.awards.get(medal)!=null){
          count++;
        }
      }
      context.fillText(Integer.toString(count), x, y-68);

      if(hoveredMedal!=null && medal!=hoveredMedal){
        context.setFillStyle("rgba(0, 0, 0, 0.5)");   
        context.fillText(""+count, x, y-68);
      }

    }
    else{
      context.setShadowOffsetX(2);
      context.setShadowOffsetY(2);

      if(starCount==1){
        context.drawImage(star, x-12, y-100+7);
      }
      else if(starCount==2){
        context.drawImage(star, x-12-25, y-100+7);
        context.drawImage(star, x-12+25, y-100+7);
      }
      else if(starCount==3){
        context.drawImage(star, x-12-30, y-100+7);
        context.drawImage(star, x-12+00, y-100+7);
        context.drawImage(star, x-12+30, y-100+7);
      }
      else if(starCount>=4){
        context.drawImage(star, x-50-5, y-100+7);
        context.drawImage(star, x-25-2, y-100+7);
        context.drawImage(star, x-00+1, y-100+7);
        context.drawImage(star, x+25+4, y-100+7);
      }
    }
  }

  void addMouseMoveHandler(MouseMoveHandler handler){
    canvas.addMouseMoveHandler(handler);
  }

  void addMouseOutHandler(MouseOutHandler handler){
    canvas.addMouseOutHandler(handler);
  }

  String toDataUrl(){
    return canvas.toDataUrl("image/png");
  }
}
