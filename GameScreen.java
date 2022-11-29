package com.badlogic.drop;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite; // Sprite demo
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
   final Drop game;

   Texture dropImage;
   Sprite bucketSprite; // Project 1

   Texture bucketImage;
   Sound dropSound;
   Music rainMusic;
   // Project 1:Ashley Zingillioglu s1310999
   Music rainMusic2;

   OrthographicCamera camera;

   Array<DropSprite> raindrops; // Project 1
   long lastDropTime;
   int dropsGathered;

   public GameScreen(final Drop gam) {
      this.game = gam;

      // load the image for the bucket, 64x64 pixels
      bucketImage = new Texture(Gdx.files.internal("bucket.png"));

      // construct sprite from bucketImage texture
      bucketSprite = new Sprite(bucketImage);

      // load the drop sound effect and the rain background "music"
      dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));

      rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
      rainMusic.setLooping(true);

      //Q2: Project 1: Ashley Zingillioglu s1310999
      rainMusic2 = Gdx.audio.newMusic(Gdx.files.internal("/Users/s1310999/Downloads//sea_theme_1.wav"));
      rainMusic2.setLooping(true);

      // create the camera and the SpriteBatch
      camera = new OrthographicCamera();
      camera.setToOrtho(false, 800, 480);

      // Project 1 - bucket sprite
      // Set initial bucket sprite position:
      bucketSprite.setX(800 / 2 - 64 / 2); // center the bucket horizontally
      bucketSprite.setY(20);
      // alternative: bucketSprite.setPosition(800 / 2 - 64 / 2, 20);

      // create the raindrops array and spawn the first raindrop
      raindrops = new Array<DropSprite>(); // Project 1
      spawnRaindrop();
   } // end constructor



   private void spawnRaindrop() {
      DropSprite raindrop = new DropSprite(); // Project 1
      float x = MathUtils.random(0, 800 - 64);
      float y = 480;
      raindrop.setPosition(x, y);
      raindrops.add(raindrop);
      lastDropTime = TimeUtils.nanoTime();
   } // end spawnRaindrop()



   @Override
   public void render(float delta) {
      // clear the screen with a dark blue color. The
      // arguments to glClearColor are the red, green
      // blue and alpha component in the range [0,1]
      // of the color to be used to clear the screen.
      Gdx.gl.glClearColor(0, 0, 0.2f, 1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

// Other drawing here.
      game.batch.end();


      // tell the camera to update its matrices.
      camera.update();

      // tell the SpriteBatch to render in the
      // coordinate system specified by the camera.
      game.batch.setProjectionMatrix(camera.combined);

      // begin a new batch and draw the bucket and
      // all drops
      game.batch.begin();
      game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);

      // Project 1 - bucket sprite
      bucketSprite.draw(game.batch);

      // Project 1
      for (DropSprite raindrop : raindrops) {
         raindrop.draw(game.batch);



      }
      game.batch.end();

      // process user input
      if (Gdx.input.isTouched()) {
         Vector3 touchPos = new Vector3();
         touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
         camera.unproject(touchPos);

         // Project 1 - bucket sprite
         bucketSprite.setX(touchPos.x - 64 / 2);
      }
      if (Gdx.input.isKeyPressed(Keys.LEFT)) {
         float newBucketX = bucketSprite.getX() - 200 * Gdx.graphics.getDeltaTime();
         bucketSprite.setX(newBucketX);
      }
      if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
         float newBucketX = bucketSprite.getX() + 200 * Gdx.graphics.getDeltaTime();
         bucketSprite.setX(newBucketX);
      }

      // keep bucket sprite within screen margins
      if (bucketSprite.getX() < 0)
         bucketSprite.setX(0);
      if (bucketSprite.getX() > 800 - 64)
         bucketSprite.setX(800 - 64);

      // check if we need to create a new raindrop

      //Q3: Project 1: Ashley Zingillioglu s1310999
      if (TimeUtils.nanoTime() - lastDropTime > 2000000000) //build test to make sure- run it back to what it was
         spawnRaindrop();

      // move the raindrops, remove any that are beneath the bottom edge of
      // the screen or that hit the bucket. In the later case we play back
      // a sound effect as well.
      Iterator<DropSprite> iter = raindrops.iterator(); // Project 1
      while (iter.hasNext()) {
         DropSprite raindrop = iter.next();
         //Q7: Project 1: Ashley Zingillioglu s1310999
         float y = raindrop.getY();
         if (y < 240) {
            y -= 300* Gdx.graphics.getDeltaTime();
         }
         if (y < 190) {
            y -= 325 * Gdx.graphics.getDeltaTime();
         }
         if (y < 90) {
            y -= 350 * Gdx.graphics.getDeltaTime();
         }
         if (y < 30) {
            y -= 400* Gdx.graphics.getDeltaTime();
         }

         // Update raindrop vertical (y) position:
         y -= 200 * Gdx.graphics.getDeltaTime();
         if (y + 64 < 0)
            iter.remove();
         else
            raindrop.setY(y);

         // Update raindrop rotation:
         raindrop.rotate();


         // Project 1 - Rectangle overlap collision detection
         Rectangle bucketRectangle = bucketSprite.getBoundingRectangle();
         Rectangle dropRectangle = raindrop.getBoundingRectangle();

         if (dropRectangle.overlaps(bucketRectangle)) {
            //Q6: Ashley Zingillioglu s1310999
            setColor((int)(Math.random()*16777215)) | (0xFF << 24);
            getColor(0, 0, 0.2f, 1);
            dropsGathered++;
            dropSound.play();
            iter.remove();
         }
      } // end while

   } //end render()
