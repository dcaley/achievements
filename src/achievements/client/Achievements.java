package achievements.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import achievements.shared.Medal;
import achievements.shared.Veteran;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class Achievements implements EntryPoint {

  private MedalCanvas canvas;
  private final AchievementServiceAsync service = GWT.create(AchievementService.class);
  private HashMap<String, Veteran> veterans = new HashMap<String, Veteran>();
  private HashMap<String, Medal> medals = new HashMap<String, Medal>();
  private CellList<String> cells = new CellList<String>(new TextCell());
  private SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
  private HTML text = new HTML();
  private DockLayoutPanel dock = new DockLayoutPanel(Style.Unit.PX);
  static final String ALL_MEDALS = "All Medals";

  @Override
  public void onModuleLoad(){

    GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){
      @Override public void onUncaughtException(Throwable e){
        e.printStackTrace();                
      }   
    });

    Document.get().getBody().getStyle().setBackgroundColor("#008000");
    Document.get().getBody().getStyle().setMargin(0, Unit.PX);

    // Old browsers can just get the hell out
    if(Canvas.createIfSupported()==null){
      return;
    }

    canvas = new MedalCanvas(veterans);

    cells.setSelectionModel(selectionModel);
    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      @Override public void onSelectionChange(SelectionChangeEvent event) {
        History.newItem(selectionModel.getSelectedObject());
        canvas.selected = selectionModel.getSelectedObject();
        resize();
      }
    });

    canvas.addMouseOutHandler(new MouseOutHandler(){
      @Override public void onMouseOut(MouseOutEvent e){
        text.setText("");
        canvas.hoveredMedal = null;
        canvas.paint();
      }   
    });

    canvas.addMouseMoveHandler(new MouseMoveHandler(){
      @Override public void onMouseMove(MouseMoveEvent e){

        Medal newHovered = canvas.getHovered(e.getX(), e.getY());
        if(canvas.hoveredMedal!=newHovered){
          canvas.hoveredMedal = newHovered;

          String message = "";
          if(newHovered!=null){

            message += "<b><font size=\"4\">"+(newHovered.name==null ? "" : newHovered.name)+":</b> ";
            message += (newHovered.description==null ? "" : newHovered.description);
            message += "<br><table border=0 cellpadding=0 cellspacing=0>";

            String selected = selectionModel.getSelectedObject();
            for(String star : veterans.get(selected).awards.get(canvas.hoveredMedal)){
              message += "<tr><td><img src=\"star.png\"></td>";
              message += "<td>"+star+"</td></tr>";
            }
            message += "</table>";

            if(selected.equals(ALL_MEDALS)){
              message += "<br><b>Recipients: </b>";
              for(Veteran v : veterans.values()){
                if(!v.shortName.equals(ALL_MEDALS) && v.awards.get(canvas.hoveredMedal)!=null){
                  message += v.shortName+" ";
                }
              }
            }
          }

          text.setHTML(message);

          canvas.paint();
        }
      }
    });   

    Window.addResizeHandler(new ResizeHandler(){
      @Override public void onResize(ResizeEvent e){
        resize();
      }
    });

    resize();

    text.getElement().getStyle().setProperty("backgroundColor", "#008000"); 
    text.getElement().getStyle().setProperty("color", "#ffca3b"); 

    FlowPanel leftPanel = new FlowPanel();
    leftPanel.getElement().getStyle().setPadding(5, Unit.PX);

    // Take a screenshot!  Doesn't work on IE.  I don't care at all.
    Button ssButton = new Button("Screenshot");
    ssButton.addClickHandler(new ClickHandler(){
      @Override public void onClick(ClickEvent event){

        MedalCanvas c = new MedalCanvas(veterans);
        c.selected = selectionModel.getSelectedObject();
        c.images = canvas.images;
        // make the screenshot a fixed width, and let it be as tall as necessary
        c.resize(655, 0);

        // display the image in a new window
        Window.open(c.toDataUrl(), "_blank", "");
      }
    });

    Style s = ssButton.getElement().getStyle();
    s.setBorderColor("black");
    s.setBackgroundColor("#ffca3b");
    s.setBackgroundImage("none");

    FlowPanel ssPanel = new FlowPanel();        
    ssPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
    ssPanel.add(ssButton);

    leftPanel.add(cells);
    leftPanel.add(ssPanel);

    dock.addWest(leftPanel, 100);
    dock.addSouth(text, 150);
    ScrollPanel sp = new ScrollPanel(canvas);
    DOM.setStyleAttribute(canvas.getParent().getElement(), "overflowX", "hidden");
    dock.add(sp);

    RootPanel.get().add(dock);

    if(GWT.isProdMode()){
      getData(); 
    }
    else{
      // doesn't do anything right now because I need to figure out a non stupid thing to do here
      service.debug(new AsyncCallback<String>() {
        @Override public void onSuccess(String result){
          getData();
        }

        @Override public void onFailure(Throwable t){
          t.printStackTrace();
        }
      });
    }  
  }

  private void loadMedalImage(final Medal m){

    /* Canvases are stupid when it comes to images.  If you pass in an image that is still being loaded, 
     * badness tends to occur.  Not sure if this is a problem with GWT or the HTML element itself.
     * So we need to wait for images to finish loading before we pass them to the canvas.
     * But we can't load the image unless it is attached to the DOM.  Weaksauce!  Ok, then we'll attach
     * it to the rootpanel and wait for it to load, then remove it after passing it to the canvas.
     */
    final Image image = new Image(m.shortName+".png");
    image.setVisible(false);
    RootPanel.get().add(image);

    image.addLoadHandler(new LoadHandler() {
      @Override public void onLoad(LoadEvent event) {
        canvas.images.put(m, (ImageElement)image.getElement().cast());
        RootPanel.get().remove(image);

        // this murders performance in dev mode, dunno why
        if(GWT.isProdMode()){
          canvas.paint();
        }
      }
    });

    image.addErrorHandler(new ErrorHandler() {
      @Override public void onError(ErrorEvent event){
        System.out.println("error loading "+image.getTitle());
        canvas.images.put(m, null);
        RootPanel.get().remove(image);
      }
    });
  }

  /* A quick word about our data format.  Each veteran has a string to record medals that looks like this:
   * 
   * medalName0|medalName1:star0:star1|medalName2:star0
   * 
   * Medals are delimited by "|", stars for a medal by ":". 
   * The first token after the "|" must correspond to the shortName of a Medal object.  Additional tokens are star descriptions.
   * 
   * Why this stupid format instead of a nice object relation that appengine is entirely capable of handling?
   * Simplicity. By making it a string I can manage these through the standard appengine datastore viewer.
   * Anything more complicated would require me to develop an entire interface just to manage these objects,
   * and that would take way more effort than it is worth.
   */
  private void getData(){

    final Veteran all = new Veteran();
    all.shortName = ALL_MEDALS;
    all.medals = "";

    service.getAllMedals(new AsyncCallback<List<Medal>>() {
      @Override public void onSuccess(List<Medal> medals){
        canvas.images = new HashMap<Medal, ImageElement>();
        for(Medal m : medals){
          loadMedalImage(m);
          Achievements.this.medals.put(m.shortName, m);
        }
        for(String s : Achievements.this.medals.keySet()){
          all.medals += s+"|";
        }

        service.getAllVeterans(new AsyncCallback<List<Veteran>>() {
          @Override
          public void onSuccess(List<Veteran> veterans){             
            HashSet<String> rowData = new HashSet<String>();

            veterans.add(0, all);

            for(Veteran v : veterans){
              rowData.add(v.shortName);
              Achievements.this.veterans.put(v.shortName, v);

              for(String s : v.medals.split("\\|")){
                String[] stars = s.split("\\:");
                String name = stars.length==0 ? s : stars[0];

                HashSet<String> starSet = new HashSet<String>();
                for(int i=1; i<stars.length; i++){
                  starSet.add(stars[i]);
                }

                v.awards.put(Achievements.this.medals.get(name), starSet);
              }
            }

            cells.setRowData(new ArrayList<String>(rowData));

            boolean validToken = cells.getVisibleItems().contains(History.getToken());
            selectionModel.setSelected(validToken ? History.getToken() : ALL_MEDALS, true);
          }

          @Override public void onFailure(Throwable t){
            t.printStackTrace();
          }
        });
      }

      @Override public void onFailure(Throwable t){
        t.printStackTrace();
      }
    });   
  }  

  // Canvas will not size itself to fit a parent container, so we'll have to handle browser resizes manually.
  private void resize(){
    int width = Window.getClientWidth();
    int height = Window.getClientHeight();
    dock.setPixelSize(width, height);

    canvas.resize(width-100-10, height-150-10);
  }
}
