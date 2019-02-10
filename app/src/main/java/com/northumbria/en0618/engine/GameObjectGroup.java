package com.northumbria.en0618.engine;

import com.northumbria.en0618.engine.opengl.IRenderable;
import com.northumbria.en0618.engine.opengl.Shader;
import com.northumbria.en0618.engine.opengl.Texture;

import java.util.ArrayList;
import java.util.List;

public class GameObjectGroup
{
    public static final int FULL_MATCH_LEVEL = 100 + 3 + 2 + 1;

    private List<GameObject> m_gameObjects = new ArrayList<>();

    private boolean m_isHUD;
    private Shader m_shader;
    private Texture m_texture;
    private boolean m_transparent;

    public GameObjectGroup(boolean isHUD, GameObject gameObject)
    {
        IRenderable renderable = gameObject.getRenderable();
        m_isHUD = isHUD;
        m_shader = renderable.getShader();
        m_texture = renderable.getTexture();
        m_transparent = renderable.isTransparent();

        m_gameObjects.add(gameObject);
    }

    public void addGameObject(GameObject gameObject)
    {
        m_gameObjects.add(gameObject);
    }

    public int matchLevel(boolean isHUD, IRenderable renderable)
    {
        int level = 0;
        level += (m_isHUD == isHUD) ? 100 : 0;
        level += (m_shader == renderable.getShader()) ? 3 : 0;
        level += (m_texture == renderable.getTexture()) ? 2 : 0;
        level += (m_transparent == renderable.isTransparent()) ? 1 : 0;
        return level;
    }

    public boolean isHUDGroup()
    {
        return m_isHUD;
    }

    public void update(float deltaTime)
    {
        int gameObjectCount = m_gameObjects.size();
        for (int i = 0; i < gameObjectCount; i++)
        {
            GameObject gameObject = m_gameObjects.get(i);
            if (gameObject.isDestroyed())
            {
                gameObjectCount--;
                m_gameObjects.set(i, m_gameObjects.get(gameObjectCount));
                m_gameObjects.remove(gameObjectCount);
                i--;
            }
            else
            {
                gameObject.update(deltaTime);
            }
        }
    }

    public void draw(float[] vpMatrix)
    {
        for (GameObject gameObject : m_gameObjects)
        {
            gameObject.draw(vpMatrix);
        }
    }

    public int size()
    {
        return m_gameObjects.size();
    }
}
