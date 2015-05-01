package net.bryanhaley.butterblitz;

public interface State
{
	public abstract void create();
	public abstract void create(String[] args);
	public abstract void create(String arg);
	public abstract void update();
	public abstract void render();
	public abstract void dispose();
}
