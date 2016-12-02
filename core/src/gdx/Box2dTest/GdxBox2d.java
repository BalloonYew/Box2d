package gdx.Box2dTest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import utils.TiledObjectUtil;


import static utils.constants.PPM;

public class GdxBox2d extends ApplicationAdapter {

    //private boolean DEBUG = false;
    private final float fSCALE = 2.0f;
    private OrthographicCamera cam;
    private OrthogonalTiledMapRenderer tmr;
    private TiledMap map;
    private Box2DDebugRenderer b2dr;
    private World world;
    private Body bplayer, bplayer2;
    private SpriteBatch batch;
    private Texture Player1;
    int nCurrentPlayer = 1;

    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        cam = new OrthographicCamera();
        cam.setToOrtho(false, w / fSCALE, h / fSCALE);

        world = new World(new Vector2(0, -9.8f), false);
        b2dr = new Box2DDebugRenderer();

        bplayer = createBox(60, 70, 32, 32, false);
        bplayer2 = createElipse(60, 150, 0.5f, false);


        batch = new SpriteBatch();
        Player1 = new Texture(Gdx.files.internal("block.png"));

        map = new TmxMapLoader().load("testMap.tmx");
        tmr = new OrthogonalTiledMapRenderer(map);

        TiledObjectUtil.parseTiledObjectLayer(world, map.getLayers().get("collision LAyer").getObjects());
    }

    @Override
    public void render() {
        update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tmr.render();

        batch.begin();
        batch.draw(Player1, bplayer.getPosition().x * PPM - (32 / 2), bplayer.getPosition().y * PPM - (32 / 2), 32, 32);
        batch.end();

        b2dr.render(world, cam.combined.scl(PPM));
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(false, width / fSCALE, height / fSCALE);
    }

    @Override
    public void dispose() {
        b2dr.dispose();
        world.dispose();
        batch.dispose();
        tmr.dispose();
        map.dispose();
    }

    public void update(float delta) {
        world.step(1 / 60f, 6, 2);

        inputUpdate(delta);
        cameraUpdate(delta);
        tmr.setView(cam);
        batch.setProjectionMatrix(cam.combined);

    }

    public void inputUpdate(float delta) {
        int horizontalForce = 0;
        int horizontalForce2 = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            horizontalForce -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            horizontalForce += 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            bplayer.applyForceToCenter(0, 300, false);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            horizontalForce2 -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            horizontalForce2 += 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            bplayer2.applyForceToCenter(0, 300, false);
        }



        bplayer.setLinearVelocity(horizontalForce * 5, bplayer.getLinearVelocity().y);
        bplayer2.setLinearVelocity(horizontalForce2 * 5, bplayer2.getLinearVelocity().y);
    }

    public void cameraUpdate(float delta) {
        //gives the camera a trail effect 
        Vector3 vPosition = cam.position;

        // a  + (b-a) * trail amount 
        // target
        // a =  the current cam position
        //b the current player posistion


        //determines which player the camera follows
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            nCurrentPlayer = nCurrentPlayer * -1;
        }
        if (nCurrentPlayer > 0) {
            vPosition.x = cam.position.x + (bplayer.getPosition().x * PPM - cam.position.x) * 0.1f;
            vPosition.y = cam.position.y + (bplayer.getPosition().y * PPM - cam.position.y) * 0.1f;
        } else {
            vPosition.x = cam.position.x + (bplayer2.getPosition().x * PPM - cam.position.x) * 0.1f;
            vPosition.y = cam.position.y + (bplayer2.getPosition().y * PPM - cam.position.y) * 0.1f;
        }
        cam.position.set(vPosition);
        cam.update();
    }

    public Body createBox(int nX, int nY, int nwidth, int nheight, boolean isStatic) {
        Body pBody;
        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        if (isStatic) {
            def.type = BodyDef.BodyType.StaticBody;
        } else {
            def.type = BodyDef.BodyType.DynamicBody;
        }
        def.position.set(nX / PPM, nY / PPM);
        def.fixedRotation = true;
        pBody = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(nwidth / 2 / PPM, nheight / 2 / PPM);


        pBody.createFixture(shape, 1.0f);
        //setting properties of the fixture
        fdef.shape = shape;
        fdef.density = 1.0f;
        fdef.friction = 0;
        fdef.restitution = 0.2f;

        pBody.createFixture(fdef);
        shape.dispose();
        return pBody;
    }

    public Body createElipse(int nX, int nY, float fRadius, boolean isStatic) {
        Body pBody;
        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        if (isStatic) {
            def.type = BodyDef.BodyType.StaticBody;
        } else {
            def.type = BodyDef.BodyType.DynamicBody;
        }
        def.position.set(nX / PPM, nY / PPM);
        def.fixedRotation = false;
        pBody = world.createBody(def);
        CircleShape shape = new CircleShape();
        //position, relative to body position
        shape.setRadius(fRadius); //radius
        pBody.createFixture(shape, 1.0f);
        //setting properties of the fixture
        fdef.shape = shape;
        fdef.density = 0.5f;
        fdef.friction = 1000f;
        fdef.restitution = 0.5f;

        pBody.createFixture(fdef);
        shape.dispose();
        return pBody;
    }
}
