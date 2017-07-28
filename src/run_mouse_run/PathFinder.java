package run_mouse_run;


import java.util.ArrayList;
import java.util.LinkedList;

class PathFinder
{
    private Map map = null;   // run_mouse_run.Map given by run_mouse_run.LevelGenerator
    private Node[][] grid = null;  // map on which we apply the search

    private Position initialPos, finalPos;

    private int mapWidth, mapHeight;

    private ArrayList<Position> shortestPath = new ArrayList<>();

    private LinkedList<Node> heap = new LinkedList<>();

    PathFinder(Map map)
    {
        this.map = map;

        initialPos = map.getPosition(0, 0);
        finalPos = map.getPosition(0, 0);

        try
        {
            setupSolver(map, initialPos, finalPos);
        }
        catch (Exception e)
        {
            System.out.println("Error setting the PathFinder ! ");
        }
    }

    ArrayList<Position> getShortestPath(Map map, Position initialPos, Position finalPos)
    {
        try
        {
            if(isSetup(map, initialPos, finalPos)) // if same request sent
                return shortestPath;

            setupSolver(map, initialPos, finalPos);

            if (!getNode(initialPos).pass || !getNode(finalPos).pass) // if it's unreachable ( a wall )
            {
                return new ArrayList<>();   // empty path
            }
            else
            {
                solve();
                clearGraph();
                return shortestPath;
            }
        }catch (Exception e)
        {
            System.out.println("Error while setting up solver : ");
            e.printStackTrace();
            return new ArrayList<>();   // empty path
        }
    }

    private boolean isSetup(Map map, Position initialPos, Position finalPos)
    {
        return this.map == map
                && Position.comparePosition(initialPos, this.initialPos)
                && Position.comparePosition(finalPos, this.finalPos);

    }

    private void cleanSolver(int width, int height)
    {
        if(grid == null || height != mapHeight || width != mapWidth)
        {
            grid = new Node[height][width];

            for (int i = 0; i < height; i++)
            {
                for (int j = 0; j < width; j++)
                {
                    grid[i][j] = new Node(map.getPosition(0,0));
                }
            }
        }
        else
        {
            clearGraph();
        }

        heap.clear();
        shortestPath.clear();
    }

    private void setupSolver(Map map, Position initialPos, Position finalPos) throws Exception
    {
        cleanSolver(map.getWidth(), map.getHeight());

        /*affect values to attributes*/
        this.map = map;
        this.mapHeight = map.getHeight();
        this.mapWidth = map.getWidth();
        this.initialPos = initialPos;
        this.finalPos = finalPos;

        /* generate Nodes from map*/
        tileToNode();

    }

    private void clearGraph()
    {
        for(int i = 0; i < grid.length; i++)
        {
            for(int j = 0; j < grid[i].length; j++)
            {
                grid[i][j].clear();
            }
        }
    }

    /**
     * Generates a grid of nodes from map
     *
     */
    private void tileToNode()
    {
        for (int i = 0; i < mapHeight; i++)
        {
            for (int j = 0; j < mapWidth; j++)
            {
                grid[i][j].setPosition(map.getPosition(j,i)); // j is x, i is y
                grid[i][j].f = -1;          // init distance to null

                if (map.getTile(j, i).isWalkable())
                    grid[i][j].setPass(true);
                else
                    grid[i][j].setPass(false);
            }
        }
    }


    /**
     * Fonction qui va dériger le Jump Point Search
     */
    private void solve()
    {
        Node current;

        // start with initial Pos node
        getNode(initialPos).updateGHFP(0, 0, null);
        heap.add(getNode(initialPos));

        /*Search loop, takes a node from heap and apply algorithm each turn ( iterative ) */
        while (true)
        {
            current = heap.pop();   //the current node is removed from the heap.

            if (Position.comparePosition(current.pos, finalPos))
            {   //if the end node is found
                shortestPath = tracePath(current);    // traceBack from final node ( with parent ) until initial
                break;   //loop is done ... yay !
            }

            ArrayList<Node> possibleSuccess = identifySuccessors(current);  //get all possible successors of the current node

            //for each one of them
            //add to the heap for later use (a possible future cur)
            heap.addAll(possibleSuccess);

            if (heap.isEmpty())
            {
                //if the heap size is 0, and we have not found our end, the end is unreachable
               // System.out.println("Path doesn't exist");
                break;   //loop is done
            }
        }
    }

    /**
     * ==========================================================
     * Functions manipulating nodes :
     * identifySuccessors : trouver les successeurs du current node ( avec jump )
     * getNeighborsPrune : find nodes that can be reached optimally from the parent of current without ever going through current node.
     * tracePath : remonter le chemin parent par parent jusqu'au premier
     * ==========================================================
     */

    /**
     * returns all nodes jumped from given node
     *
     * @param node (Node) the current node which we identify successors ( after pruning )
     * @return all nodes jumped from given node
     */
    private ArrayList<Node> identifySuccessors(Node node)
    {
        ArrayList<Node> successors = new ArrayList<>();          //successors list to be returned

        ArrayList<Position> neighbors = getNeighborsPrune(node);    //all neighbors after pruned

        for(Position neighbor : neighbors)
        { //for each of these neighbors
            Position tmp;

            tmp = jump(neighbor, node.pos); //get next jump point from that neighbor

            if (tmp != null)
            {   //if that point is not null
                // ... not my code .. A* stuff u_u
                int x = tmp.getPosX();
                int y = tmp.getPosY();
                float ng = (toPointApprox(x, y, node.pos.getPosX(), node.pos.getPosY()) + node.g);   //get the distance from start
                if (getNode(x, y).f <= 0 || getNode(x, y).g > ng)
                {  //if this node is not already found, or we have a shorter distance from the current node
                    getNode(x, y).updateGHFP(toPointApprox(x, y, node.pos.getPosX(), node.pos.getPosY()) + node.g, toPointApprox(x, y, finalPos.getPosX(), finalPos.getPosY()), node); //then update the rest of it
                    successors.add(getNode(x, y));  //add this node to the successors list to be returned
                }
            }
        }
        return successors;  // return successors
    }

    /**
     * Returns nodes that should be jumped based on the parent location in relation to the given node.
     *
     * @param node (Node) node which has a parent (not the start node)
     * @return (ArrayList<Node>) list of nodes that will be jumped
     */

    private ArrayList<Position> getNeighborsPrune(Node node)
    {
        Node parent = node.parent;    //get parent node for position (x,y)
        int x = node.pos.getPosX();
        int y = node.pos.getPosY();
        int px, py, dx, dy;
        ArrayList<Position> neighbors = new ArrayList<>();

        /*directed pruning: can ignore most neighbors, unless forced*/
        if (parent != null)
        {
            px = parent.pos.getPosX();
            py = parent.pos.getPosY();
            //get the normalized direction of travel ... des fois faut pas t7awess tafham ^_^
            dx = (x - px) / Math.max(Math.abs(x - px), 1);  // x-px/abs(x-px), added max 1 to not divide by 0
            dy = (y - py) / Math.max(Math.abs(y - py), 1);

            //Si on va en diagonale
            if (dx != 0 && dy != 0)
            {
                // Up ( or down )
                if (getNode(x, y + dy).pass)
                {
                    neighbors.add(map.getPosition(x, y + dy));
                }
                // Right ( or left )
                if (getNode(x + dx, y).pass)
                {
                    neighbors.add(map.getPosition(x + dx, y));
                }
                // diagonale
                if ((getNode(x, y + dy).pass || getNode(x + dx, y).pass) && getNode(x + dx, y + dy).pass)
                {
                    neighbors.add(map.getPosition(x + dx, y + dy));
                }
                if (!getNode(x - dx, y).pass && getNode(x, y + dy).pass)
                {
                    neighbors.add(map.getPosition(x - dx, y + dy));
                }
                if (!getNode(x, y - dy).pass && getNode(x + dx, y).pass)
                {
                    neighbors.add(map.getPosition(x + dx, y - dy));
                }
            } else
            {
                // vertical
                if (dx == 0)
                {
                    if (getNode(x, y + dy).pass)
                    {
                        if (getNode(x, y + dy).pass)
                        {
                            neighbors.add(map.getPosition(x, y + dy));
                        }
                        if (!getNode(x + 1, y).pass)
                        {
                            neighbors.add(map.getPosition(x + 1, y + dy));
                        }
                        if (!getNode(x - 1, y).pass)
                        {
                            neighbors.add(map.getPosition(x - 1, y + dy));
                        }
                    }
                }
                // horizontal
                else
                {
                    if (getNode(x + dx, y).pass)
                    {
                        if (getNode(x + dx, y).pass)
                        {
                            neighbors.add(map.getPosition(x + dx, y));
                        }
                        if (!getNode(x, y + 1).pass)
                        {
                            neighbors.add(map.getPosition(x + dx, y + 1));
                        }
                        if (!getNode(x, y - 1).pass)
                        {
                            neighbors.add(map.getPosition(x + dx, y - 1));
                        }
                    }
                }
            }
        } else
        {
            //return all neighbors
            return getNeighbors(node); //adds initial nodes to be jumped from!
        }

        return neighbors; //this returns the neighbors, you know ... 17 "if" à 2h du mat' is bad for health ...
    }

    /**
     * trace back the path from parent nodes
     * @param current (Node) : the final position node ( current when finished JPS )
     * @return connected traced Path ( apply connectPath to result trail )
     */
    private ArrayList<Position> trail = new ArrayList<Position>();

    private ArrayList<Position> tracePath(Node current) {
        trail.clear();
        while (current.parent != null)
        {
            try
            {
                trail.add(current.pos);
            } catch (Exception e)
            {
                // ;_;
               System.out.println("trace path exception");
            }
            Node tmp = current;
            current = current.parent;
            tmp.parent = null; // to prevent looping when going back, just in case
        }
        trail.add(current.pos);
        //System.out.println("Path Trace Complete!");
        return connectPath(trail);
    }

    /**
     * Connect path by adding intermediate cases between jump points
     * Reverse the path so that the first position is the initial
     * @param path the tracePath
     * @return connected path with initial position first
     */
    private ArrayList<Position> connectPath(ArrayList<Position> path)
    {
        if(path.isEmpty()) return path;

        ArrayList<Position> connectedPath = new ArrayList<>();

        // add first point
        connectedPath.add(0,path.get(0));

        for(int i = 0; i < path.size()-1; i++)
        {
            // take two points
            Position begin = path.get(i), end = path.get(i+1);

            // check if connected
            if(Math.abs(begin.getPosX()-end.getPosX()) <= 1
                    && Math.abs(begin.getPosY() - end.getPosY()) <= 1)
            {
                // add the next point
                connectedPath.add(0, end);
            }
            else
            {
                /* find all intermediate points */
                //get direction between points
                int dx = (end.getPosX() - begin.getPosX()) / Math.max(Math.abs(end.getPosX() - begin.getPosX()), 1);    // max to not devide by 0
                int dy = (end.getPosY() - begin.getPosY()) / Math.max(Math.abs(end.getPosY() - begin.getPosY()), 1);

                // connect points
                Position tmp = begin;   // start at initial pos
                do
                {
                    tmp = map.getPosition(
                            tmp.getPosX() + dx, // add 0/1 step in direction of end node
                            tmp.getPosY() + dy // add 0/1 step in direction of end node
                    );
                    connectedPath.add(0, tmp);    // add intermediate position at beggining ( reverse path )

                }while ( ! Position.comparePosition(tmp, end) && getNode(tmp).pass); // repeat until begin reaches end pos
                // note : at the end of the loop, end is already added ( tmp == end )
            }
        }
        // fix : remove the first position from the path
        connectedPath.remove(0);
        // finally, return the path
        return connectedPath;
    }

    /**
     * jump method recursively searches in the direction of parent (px,py) to child, the current node (x,y).
     * It will stop and return its current position in three situations:
     *
     * 1) The current node is the end node. (endX, endY)
     * 2) The current node is a forced neighbor.
     * 3) The current node is an intermediate step to a node that satisfies either 1) or 2)
     *
     * @param neighbor (run_mouse_run.Position) current node
     * @param parent   (run_mouse_run.Position) current.parent
     * @return run_mouse_run.Position of node which satisfies one of the conditions above, or null if no such node is found.
     */
    private Position jump(Position neighbor, Position parent) {
        Position jx; //used to later check if full or null
        Position jy; //used to later check if full or null

        int x = neighbor.getPosX(), y = neighbor.getPosY();
        int px = parent.getPosX(), py = parent.getPosY();

        // find direction
        int dx = (x - px) / Math.max(Math.abs(x - px), 1); //because parents aren't always adjacent, this is used to find parent -> child direction (for x)
        int dy = (y - py) / Math.max(Math.abs(y - py), 1); //because parents aren't always adjacent, this is used to find parent -> child direction (for y)

        if (!getNode(neighbor).pass)
        { //if this space is not grid.walkable, return a null.
            return null;
        }
        if (Position.comparePosition(neighbor,finalPos))
        {   //if end point, return that point. The search is over! YAY xD
            return neighbor;
        }
        if (dx != 0 && dy != 0)
        {  //if x and y both changed, we are on a diagonally adjacent square: here we check for forced neighbors on diagonals
            if ((getNode(x - dx, y + dy).pass && !getNode(x - dx, y).pass) || //we are moving diagonally, we don't check the parent, or our next diagonal step, but the other diagonals
                    (getNode(x + dx, y - dy).pass && !getNode(x, y - dy).pass))
            {  //if we find a forced neighbor here, we are on a jump point, and we return the current position
                return neighbor;
            }
        } else
        { //check for horizontal/vertical
            if (dx != 0)
            { //moving along x
                if ((getNode(x + dx, y + 1).pass && !getNode(x, y + 1).pass) || //we are moving along the x axis
                        (getNode(x + dx, y - 1).pass && !getNode(x, y - 1).pass))
                {  //we check our side nodes to see if they are forced neighbors
                    return neighbor;
                }
            } else
            {
                if ((getNode(x + 1, y + dy).pass && !getNode(x + 1, y).pass) ||  //we are moving along the y axis
                        (getNode(x - 1, y + dy).pass && !getNode(x - 1, y).pass))
                {     //we check our side nodes to see if they are forced neighbors
                    return neighbor;
                }
            }
        }

        if (dx != 0 && dy != 0)
        { //when moving diagonally, must check for vertical/horizontal jump points
            jx = jump(map.getPosition(x + dx, y), map.getPosition(x, y));
            jy = jump(map.getPosition(x, y + dy), map.getPosition(x, y));

            if (jx != null || jy != null)
            {
                return neighbor;
            }
        }
         if (getNode(x + dx, y).pass || getNode(x, y + dy).pass)
        { //moving diagonally, must make sure one of the vertical/horizontal neighbors is open to allow the path
            return jump(map.getPosition(x + dx, y + dy), map.getPosition(x, y));
        } else
        { //if we are trying to move diagonally but we are blocked by two touching corners of adjacent nodes, we return a null
            return null;
        }
    }

    /**
     * returns all adjacent nodes that can be traversed
     *
     * @param node (Node) finds the neighbors of this node
     * @return (ArrayList<run_mouse_run.Position>) list of neighbors that can be traversed
     */
    private ArrayList<Position> getNeighbors(Node node) {
        ArrayList<Position> neighbors = new ArrayList<>();
        int x = node.pos.getPosX();
        int y = node.pos.getPosY();

        boolean d0 = false; //These booleans are for speeding up the adding of nodes.
        boolean d1 = false;
        boolean d2 = false;
        boolean d3 = false;

        if (getNode(x, y - 1).pass)
        {
            neighbors.add(map.getPosition(x, y - 1));
            d0 = d1 = true;
        }
        if (getNode(x + 1, y).pass)
        {
            neighbors.add(map.getPosition(x + 1, y));
            d1 = d2 = true;
        }
        if (getNode(x, y + 1).pass)
        {
            neighbors.add(map.getPosition(x, y + 1));
            d2 = d3 = true;
        }
        if (getNode(x - 1, y).pass)
        {
            neighbors.add(map.getPosition(x - 1, y));
            d3 = d0 = true;
        }
        if (d0 && getNode(x - 1, y - 1).pass)
        {
            neighbors.add(map.getPosition(x - 1, y - 1));
        }
        if (d1 && getNode(x + 1, y - 1).pass)
        {
            neighbors.add(map.getPosition(x + 1, y - 1));
        }
        if (d2 && getNode(x + 1, y + 1).pass)
        {
            neighbors.add(map.getPosition(x + 1, y + 1));
        }
        if (d3 && getNode(x - 1, y + 1).pass)
        {
            neighbors.add(map.getPosition(x - 1, y + 1));
        }
        return neighbors;
    }

    private Node getNode(Position pos) {
        try
        {
            return grid[pos.getPosY()][pos.getPosX()];
        } catch (Exception e)
        {
            //System.out.println("Null node");
            Node n = new Node(map.getPosition(-1, -1));
            n.setPass(false);
            return n;

        }
    }

    private Node getNode(int x, int y) {
        try
        {
            return grid[y][x];
        } catch (Exception e)
        {
            //System.out.println("Null node ("+x+","+y+")" + heap.size());
            Node n = new Node(map.getPosition(-1, -1));
            n.setPass(false);
            return n;
        }
    }

    private float toPointApprox(float x, float y, int tx, int ty) {
        return (float) Math.sqrt(Math.pow(Math.abs(x - tx), 2) + Math.pow(Math.abs(y - ty), 2));
    }

    private class Node {
        Position pos;
        float g, h, f;  //g = from start; h = to end, f = both together
        boolean pass;
        Node parent;

        Node(Position pos)
        {
            this.pos = pos;
            parent = null;
            this.pass = true;
            g = h = f = 0;
        }

        void updateGHFP(float g, float h, Node parent) {
            this.parent = parent;
            this.g = g;
            this.h = h;
            f = g + h;
        }

        void setPass(boolean pass)
        {
            this.pass = pass;
        }

        public void clear()
        {
            parent = null;
            this.pass = false;
            g = h = f = 0;
        }

        public void setPosition(int x, int y) {
            pos = map.getPosition(x,y);
        }

        public void setPosition(Position position) {
            this.pos = position;
        }
    }

}
