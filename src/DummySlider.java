import processing.core.PApplet;
import controlP5.ControlP5;
import controlP5.Slider;
import controlP5.Toggle;


public class DummySlider extends PApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9113911688132028630L;
	ControlP5 cp5;
	Slider slides;
	Toggle toggle;
	
	public void setup() {
		size(200,200);
		
		cp5 = new ControlP5(this);
		
		slides = cp5.addSlider("slides")
			.setPosition(25,100)
			.setSize(100, 25)
			.setRange(0, 100)
			;
		
		toggle = cp5.addToggle("toggle")
				.setPosition(25, 150)
				.setSize(50, 20)
				;
		
	}
	
	public void draw() {
		//System.out.println(slides.getValue());
	}
	
	
	public void toggle() {
		System.out.println(toggle.getState());
	}
	

}
