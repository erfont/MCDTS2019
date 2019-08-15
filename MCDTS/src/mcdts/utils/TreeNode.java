package mcdts.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TreeNode {
    static Random r = new Random();
    static int nActions = 5;
    static double epsilon = 1e-6;

    TreeNode[] children;
    double nVisits, totValue;
    
    //MY TREE IS A NonTerminalSymbol INSTANCE. TRY INHERITING FROM IT CREATING MCTSNONTERMINALSYMBOL TO ADD MCTS RELATED FUNCTIONALITIES

    public void selectAction() { //SELECTACTION IS CALLED REPEATEDLY FROM OUTSIDE. TREENODE GROWS WITH EVERY CALL
        List<TreeNode> visited = new LinkedList<TreeNode>();
        TreeNode cur = this;
        visited.add(this);
        while (!cur.isLeaf()) { //WHILE CURRENT NODE IS NOT A LEAF, CALL SELECT ON IT
            cur = cur.select(); //SELECT WILL SELECT AND RETURN A CHILD, ASSIGNED AS THE NEW CURRENT 
            visited.add(cur);
        }
        cur.expand(); //AT THIS POINT, CURRENT IS A LEAF NODE CHOSEN BY THE SELECTION POLICY. THEREFORE, IT'S EXPANDED
        TreeNode newNode = cur.select(); //SELECT WILL SELECT AND RETURN A CHILD, ASSIGNED AS NEWNODE (THE NODE TO BE ROLLED OUT)
        visited.add(newNode);
        double value = rollOut(newNode); //ROLLOUT NEWNODE, RETURNING A SCORE
        for (TreeNode node : visited) { //UPDATE ALL VISITED NODES
            // would need extra logic for n-player game
            node.updateStats(value);
        }
    }

    public void expand() {
        children = new TreeNode[nActions];
        for (int i=0; i<nActions; i++) {
            children[i] = new TreeNode();
        }
    }

    private TreeNode select() {
        TreeNode selected = null;
        double bestValue = Double.MIN_VALUE;
        for (TreeNode c : children) {
            double uctValue = c.totValue / (c.nVisits + epsilon) +
                       Math.sqrt(Math.log(nVisits+1) / (c.nVisits + epsilon)) +
                           r.nextDouble() * epsilon;
            // small random number to break ties randomly in unexpanded nodes
            if (uctValue > bestValue) {
                selected = c;
                bestValue = uctValue;
            }
        }
        return selected;
    }

    public boolean isLeaf() {
        return children == null;
    }

    public double rollOut(TreeNode tn) {
        // ultimately a roll out will end in some value
        // assume for now that it ends in a win or a loss
        // and just return this at random
        return r.nextInt(2);
    }

    public void updateStats(double value) {
        nVisits++;
        totValue += value;
    }

    public int arity() {
        return children == null ? 0 : children.length;
    }
}
