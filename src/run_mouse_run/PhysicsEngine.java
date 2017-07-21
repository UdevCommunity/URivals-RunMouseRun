package run_mouse_run;

class PhysicsEngine
{
    private Map levelMap;

    PhysicsEngine()
    {
        levelMap = GameManager.gameManager.getLevelGenerator().getMap();
    }

    void update()
    {
        // Check in this order, if a cat is hiding behind a cheese and mouse reach it, mouse win
        checkIsMouseOnSpecialTile();
        checkIsCatsOnSpecialTile();
    }

    private void checkIsCatsOnSpecialTile()
    {
        for (Cat cat: GameManager.gameManager.getCats())
        {
            if(!cat.isAlive())
                continue;

            Tile tileCatIsStandingOn = levelMap.getTile(cat.getPosition().getPosX(), cat.getPosition().getPosY());

            switch (tileCatIsStandingOn)
            {
                case MOUSE:
                    cat.increaseTargetReachedCount();

                    for (Mouse dyingMouse: GameManager.gameManager.getMouses())
                    {
                        if(Position.comparePosition(cat.getPosition(), dyingMouse.getPosition()))
                        {
                            dyingMouse.die();
                            GameManager.gameManager.chekEndGameConditions("Cats Win", cat.getName());
                            break;
                        }
                    }

                    break;
                case MINE:
                    GameManager.gameManager.getDrawEngine().explodeMine(cat.getPosition().getPosX(), cat.getPosition().getPosY());
                    levelMap.setTile(cat.getPosition().getPosX(), cat.getPosition().getPosY(), Tile.EMPTY);
                case WALL:
                    cat.respawn();
                    break;
                case POWERUP_VISION:
                    cat.applyVisionPowerUp();
                    GameManager.gameManager.getLevelGenerator().spawnVisionPowerup();
                    break;
                case POWERUP_SPEED:
                    cat.applyMoveSpeedPowerup();
                    GameManager.gameManager.getLevelGenerator().spawnSpeedPowerup();
                    break;
            }

            if(tileCatIsStandingOn == Tile.POWERUP_SPEED || tileCatIsStandingOn == Tile.POWERUP_VISION
                    || tileCatIsStandingOn == Tile.EMPTY || tileCatIsStandingOn == Tile.MOUSE)
                levelMap.setTile(cat.getPosition().getPosX(), cat.getPosition().getPosY(), Tile.CAT);
        }
    }

    private void checkIsMouseOnSpecialTile()
    {
        for (Mouse mouse: GameManager.gameManager.getMouses())
        {
            if (!mouse.isAlive())
                continue;

            Tile tileMouseIsStandingOn = levelMap.getTile(mouse.getPosition().getPosX(), mouse.getPosition().getPosY());

            switch (tileMouseIsStandingOn)
            {
                case CHEESE:
                    mouse.increaseTargetReachedCount();
                    GameManager.gameManager.chekEndGameConditions("Mouses Win", mouse.getName());
                    break;

                case CAT:
                    for (Cat winningCat: GameManager.gameManager.getCats())
                    {
                        if(Position.comparePosition(mouse.getPosition(), winningCat.getPosition()))
                        {
                            winningCat.increaseTargetReachedCount();
                            mouse.die();
                            GameManager.gameManager.chekEndGameConditions("Cats Win", winningCat.getName());
                            break;
                        }
                    }

                    break;
                case MINE:
                    GameManager.gameManager.getDrawEngine().explodeMine(mouse.getPosition().getPosX(), mouse.getPosition().getPosY());
                    levelMap.setTile(mouse.getPosition().getPosX(), mouse.getPosition().getPosY(), Tile.EMPTY);
                case WALL:
                    mouse.respawn();
                    break;
                case POWERUP_VISION:
                    mouse.applyVisionPowerUp();
                    GameManager.gameManager.getLevelGenerator().spawnVisionPowerup();
                    break;
                case POWERUP_SPEED:
                    mouse.applyMoveSpeedPowerup();
                    GameManager.gameManager.getLevelGenerator().spawnSpeedPowerup();
                    break;
            }

            if(tileMouseIsStandingOn == Tile.POWERUP_SPEED || tileMouseIsStandingOn == Tile.POWERUP_VISION
                    || tileMouseIsStandingOn == Tile.EMPTY || tileMouseIsStandingOn == Tile.CHEESE)
                levelMap.setTile(mouse.getPosition().getPosX(), mouse.getPosition().getPosY(), Tile.MOUSE);
        }
    }
}
