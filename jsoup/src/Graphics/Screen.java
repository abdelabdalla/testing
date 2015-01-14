package Graphics;

import java.util.ArrayList;

import Connection.Client;
import Entity.Enemy;
import Entity.Objects;
import Main.Display;
import Main.Game;
import Model.Face;
import Model.Model;

public class Screen extends Render{
	public Render3D render;
	Display d;
	public ArrayList<double[]> positions = new ArrayList<double[]>();
	public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public ArrayList<Objects> objects = new ArrayList<Objects>();
	public ArrayList<Objects> bullets = new ArrayList<Objects>();
	public ArrayList<Model> models = new ArrayList<Model>();
	int width, height;
	
	Texture player = new Texture("/textures/Ground2.png");
	Texture enemy = new Texture("/textures/Enemy.png");
	Texture object = new Texture("/textures/Ground4.png");
	Texture bullet = new Texture("/textures/Fire.png");

	public Screen(int width, int height, Display d) {	
		super(width, height);
		this.width = width;
		this.height = height;
		this.d = d;
		render = new Render3D(width, height, d);
		models.add(new Model());//TODO implement properly
		models.get(models.size()-1).LoadModel("untitled");
	}

	public void render(Game game) {
		for (int i = 0; i < width * height; i++) {
			pixels[i] = 0;
		}
		
		d.activebullets = 0;
		render.floor(game);
		//RenderObjects();
		render.renderDistanceLimiter();
		draw(render, 0, 0);
	}
	
	private void RenderObjects() {
		for(double[] v3f : positions){
			if(positions.indexOf(v3f) != Client.clientnumber-1){
			renderBlock(v3f[0]/8,v3f[1]/8,v3f[2]/8, 1, 0.5, 1, player);
			}
		}
		for(Enemy e : enemies){
			if(!e.dead){
			renderBlock(e.x/8,e.y/8,e.z/8, 1, e.displayhealth, 1, enemy);
			}
		}
		for(Objects e : objects){
			renderBlock(e.x/8,e.y/8,e.z/8, 1, 1, 1, object);
		}
		for(Objects e : bullets){
			if(!e.flash){
			d.activebullets++;
			}
			if(e.maxdistreached){
			d.activebullets--;
			}else{
				if(e.bullet)
					renderBlock(e.x/8,e.y/8,e.z/8, 0.05, 0.025, 0.05, bullet);
				else if(e.flash)
					renderBlock(e.x/8,e.y/8,e.z/8, 0.1, 0.1, 0.1, bullet);
			}
		}	
		for(Model m : models){
			for(Face f : m.model){
				render.renderFace(f, f.t);
			}
		}
	}

	public void CheckCollision(int p) {
		d.enemiesattacking = 0;
		d.collisionright = false;
		d.collisionback = false;
		d.collisionfront = false;
		d.collisionleft = false;
		
		for(Enemy e : enemies){
			double x1, y1, z1;
			x1 = e.x;
			y1 = e.y;
			z1 = e.z;
			
			if(!e.dead){		
				if(x1 >= d.x - 16 && x1 <= d.x + 8 && z1 >= d.z - 16 && z1 <= d.z + 8){
					e.chase = false;
					e.attacking = true;
					if(d.HEALTH > 0 && p != 1){
					d.HEALTH --;
					}
					d.enemiesattacking++;
					
					//TODO: needs fixing ->
					/* Not relative to direction facing
					 * Use sine/cosine? 
					 * Remake but using xMove and yMove?
					 */
					if(x1 >= d.x){
						d.collisionright = true;
					}
					if(x1 <= d.x){
						d.collisionleft = true;
					}
					if(z1 >= d.z){
						d.collisionfront = true;
					}
					if(z1 <= d.z){
						d.collisionback = true;
					}
				}else{
					e.chase = true;
					e.attacking = false;
				}
			}
			
			for(Objects e2 : bullets){
				double x2, y2, z2;	
				x2 = e2.x;
				y2 = e2.y;
				z2 = e2.z;
					
				if(x2 >= x1 && x2 <= x1 + 8 && z2 >= z1 && z2 <= z1 + 8 && y2 >= y1 -8 && y2 <= y1 +8 && e2.bullet){
					if(e2.canhurt(e)){
						double dmgtodo = (d.WeaponDamage+(Math.random()*3)-1);
						e.health-=dmgtodo;
						d.PlaySound("/audio/Enemy_Hit.wav");
					}
					e2.canthurt(e);
				}
			}
		}
	}
	
	public void tick(){
		for(Enemy e : enemies){
			e.tick();
		}
		for(Objects e : bullets){
			e.tick();
		}
		CheckCollision(0);
	}
	
	public void renderBlock(double x, double y, double z, double sizex, double sizey, double sizez, Texture t){	
		if(sizey > 0.5){
			
			render.renderWall(x, 	     x,         z + sizez, z,         0.5, y, t);//left
			render.renderWall(x + sizex, x + sizex, z,         z + sizez, 0.5, y, t);//right
			render.renderWall(x,         x + sizex, z,         z,         0.5, y, t);//front
			render.renderWall(x + sizex, x,         z + sizez, z + sizez, 0.5, y, t);//back
			
			render.renderWall(x + sizex, x + sizex, z,         z + sizez, 0.5, y+sizey/2, t);
			render.renderWall(x + sizex, x,         z + sizez, z + sizez, 0.5, y+sizey/2, t);
			render.renderWall(x,         x,         z + sizez, z,         0.5, y+sizey/2, t);
			render.renderWall(x,         x + sizex, z,         z,         0.5, y+sizey/2, t);
			
		}
		else
		{
			render.renderWall(x, 	     x,         z + sizez, z,         sizey, y, t);//left
			render.renderWall(x + sizex, x + sizex, z,         z + sizez, sizey, y, t);//right
			render.renderWall(x,         x + sizex, z,         z,         sizey, y, t);//front
			render.renderWall(x + sizex, x,         z + sizez, z + sizez, sizey, y, t);//back
		}
	}

}
