package gdx.Box2dTest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class GdxBox2d extends ApplicationAdapter {

    private boolean DEBUG = false;
    private OrthographicCamera cam;
    private Box2DDebugRenderer b2dr;
    private World world;
    private Body player;

    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        cam = new OrthographicCamera();
        cam.setToOrtho(false, w / 2, h / 2);

        world = new World(new Vector2(0, -9.8f), false);
        b2dr = new Box2DDebugRenderer();
        
        player = createPlayer();
        

    }

    @Override
    public void render() {
        update(Gdx.graphics.getDeltaTime());


        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        b2dr.render(world,cam.combined);
        
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(false, width / 2, height / 2);
    }

    @Override
    public void dispose() {
        b2dr.dispose();
        world.dispose();
    }

    public void update(float delta) {
        world.step(1/60f,6,2);
        cameraUpdate(delta);
        
    }
    public void cameraUpdate(float delta){
        Vector3 position = cam.position;
        position.x = player.getPosition().x;
        position.y =player.getPosition().y;
        cam.position.set(position);
        cam.update();
    }

    public Body createPlayer() {
        Body pBody;
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(0, 0);
        def.fixedRotation = true;
        pBody = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(32 / 2, 32 / 2);

        pBody.createFixture(shape, 1.0f);
        shape.dispose();
        return pBody;
    }
}
