import controlP5.*;


public class Controls {

	ControlP5 cp5;
	MainGUI gui;
	private int height = 400;
	private int width = 300;
	Toggle grav_toggle, repulsion_toggle;
	Button step_button, play_button, user_control;
	boolean gravity = true;
	boolean repulsion = true;
	public Slider inellastic_slider, radius_slider, particleCount_slider, damping_slider, gravity_slider, repulsion_slider;
	float inellasticCollision, radius_float, damp_float;
	int particleCount = 500;
	int sliderValue = 120;
	float gravity_mag = (float) 9.81;
	boolean pause = true;
	boolean single_step, user_control_bool;
	
	@SuppressWarnings("static-access")
	public Controls(MainGUI gui) {
		this.gui = gui;
		
		cp5 = new ControlP5(gui);
		
		cp5.getWindow().setPositionOfTabs(gui.sim.width+1, 0);
		
		cp5.addTab("Forces");
		
		cp5.getTab("default")
			.setLabel("Simulation")
			.setId(1);
		
		cp5.getTab("Forces")
			.setId(2);
		
		
		//GLOBAL CONTROLLERS
		
		play_button = cp5.addButton("Play")
				.setPosition((Simulation.width/4)-30, Simulation.height + 35)
				.setSize(60, 20)
				.setBroadcast(true)
				.setSwitch(true);
		
		if (pause) 
			play_button.setCaptionLabel("Play");
		else
			play_button.setCaptionLabel("Pause");
		
		cp5.getController("Play").getCaptionLabel().align(ControlP5.CENTER,ControlP5.CENTER);
		
		step_button = cp5.addButton("Single Step")
				.setPosition((2*Simulation.width/4)-30, Simulation.height + 35)
				.setSize(60, 20);
		
		cp5.getController("Single Step").getCaptionLabel().align(ControlP5.CENTER,ControlP5.CENTER);
		
		user_control = cp5.addButton("User Control")
				.setPosition((3*Simulation.width/4)-30, Simulation.height + 35)
				.setSize(60, 20)
				.setSwitch(true);
		
		//SIMULATION TAB
		
		particleCount_slider = cp5.addSlider("Particle Count")
				.setPosition(Simulation.width + width/2-100, 45)
				.setSize(200, 20)
				.setValue(particleCount)
				.setRange(0, 1500);
		
		cp5.getController("Particle Count").getCaptionLabel().align(ControlP5.RIGHT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
		
		
		//FORCES TAB
		grav_toggle = cp5.addToggle("Gravity")
			.setPosition(Simulation.width + (width/3)-25, 35)
			.setSize(50, 20)
			.setValue(gravity)
			.setMode(ControlP5.SWITCH);
		
		repulsion_toggle = cp5.addToggle("Repulsion")
				.setPosition(Simulation.width + (2*width/3)-25, 35)
				.setSize(50, 20)
				.setValue(repulsion)
				.setMode(ControlP5.SWITCH);
		
		inellastic_slider = cp5.addSlider("Inellastic Collision")
			.setPosition(Simulation.width + width/2-100, 335)
			.setSize(200, 20)
			.setRange(0, 1)
			.setValue(gui.sim.inellastic);
		
		cp5.getController("Inellastic Collision").getCaptionLabel().align(ControlP5.RIGHT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
		
		
		radius_slider = cp5.addSlider("Repulsion Radius")
				.setPosition(Simulation.width + width/2-100, 215)
				.setSize(200, 20)
				.setValue(50)
				.setRange(0, 100);
		
		cp5.getController("Repulsion Radius").getCaptionLabel().align(ControlP5.RIGHT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
		
		repulsion_slider = cp5.addSlider("Repulsion Strength")
				.setPosition(Simulation.width + width/2-100, 155)
				.setSize(200, 20)
				.setValue(50)
				.setRange(0, 100);
		
		cp5.getController("Repulsion Strength").getCaptionLabel().align(ControlP5.RIGHT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
		
		damping_slider = cp5.addSlider("Viscous Damping")
				.setPosition(Simulation.width + width/2-100, 275)
				.setSize(200, 20)
				.setValue(gui.sim.damp)
				.setRange(0, 1);
		
		cp5.getController("Viscous Damping").getCaptionLabel().align(ControlP5.RIGHT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
		
		gravity_slider = cp5.addSlider("Gravity Strength")
				.setPosition(Simulation.width + width/2-100, 95)
				.setSize(200, 20)
				.setValue(gravity_mag)
				.setRange(0, 25);
		
		cp5.getController("Gravity Strength").getCaptionLabel().align(ControlP5.RIGHT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
		
		
		//ARANGE CONTROLLERS INTO THEIR TABS
		cp5.getController("Gravity").moveTo("Forces");
		cp5.getController("Repulsion").moveTo("Forces");
		cp5.getController("Inellastic Collision").moveTo("Forces");
		cp5.getController("Repulsion Radius").moveTo("Forces");
		cp5.getController("Repulsion Strength").moveTo("Forces");
		cp5.getController("Viscous Damping").moveTo("Forces");
		cp5.getController("Gravity Strength").moveTo("Forces");
		
		//SET GLOBAL CONTROLLERS
		cp5.getController("Play").moveTo("global");
		cp5.getController("Single Step").moveTo("global");
		cp5.getController("User Control").moveTo("global");

		
		
	}

	public void draw() {
		
		//gui.background(150);
		gui.fill(150);
		gui.stroke(150);
		gui.rect(Simulation.width + 1, 0, width, height);
		gui.rect(0, Simulation.height + 1, Simulation.width + width, 100);
		
		gui.stroke(00);
		
		
		inellasticCollision = inellastic_slider.getValue();
		gravity = grav_toggle.getState();
		gravity_mag = gravity_slider.getValue();
		repulsion = repulsion_toggle.getState();
		radius_float = radius_slider.getValue();
		particleCount = (int) particleCount_slider.getValue();
		gui.sim.updateParticleCount(particleCount);
		damp_float = damping_slider.getValue();
		pause = play_button.getBooleanValue();
		single_step = step_button.getBooleanValue();
		user_control_bool = user_control.getBooleanValue();
		
		if (pause) 
			play_button.setCaptionLabel("Play");
		else
			play_button.setCaptionLabel("Pause");
		
		particleCount_slider.getValueLabel().setText(String.format("%.0f" , particleCount_slider.getValue()));

	}

}
	
