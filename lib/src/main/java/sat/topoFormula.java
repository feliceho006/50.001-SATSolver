package sat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.NegLiteral;
import sat.formula.PosLiteral;

public class topoFormula {
    private boolean is2SAT;
    private Graph graph;

    public topoFormula(){
        is2SAT = true;
        graph = new Graph();
    }


    public boolean is2SAT() {
        return is2SAT;
    }

    public Graph getGraph(){
        return graph;
    }


    public Formula readFile(String filename){
        System.out.println("Reading file: " + filename);
        File fin;
        Scanner bin = null;
        Formula f = new Formula(); //create and instance of the formula

        try {
            fin = new File(filename);
            bin = new Scanner(fin);
            String line;

            boolean commentCheck=true;
            while(bin.hasNextLine()){
                String commentRemove=bin.nextLine();
                if(commentRemove.startsWith("p") || commentRemove.startsWith("P")){
                    commentCheck = false;
                    break;
                }else{
                    commentCheck = true;
                }
            }


            if (commentCheck == false){
                bin.useDelimiter(" 0");
                while (bin.hasNext()) {
                    line=bin.next();
                    if(line.length()>0){
                        String[] tempLine= line.trim().split("\\s+");
                        if (tempLine.length >2){
                            is2SAT = false;
                        }
                        Clause c = new Clause();

                        for(String i:tempLine){

                            if(Integer.parseInt(i)==0){
                                break;
                            }

                            Literal literal = PosLiteral.make(Integer.toString(Math.abs(Integer.parseInt(i))));//makes literal instance

                            if((Integer.parseInt(i))<0){ //add the negated Integer to the clause if string is negative
                                c=c.add(literal.getNegation());
                            }
                            else if ((Integer.parseInt(i))>0){ //add the postitive Integer to the clause if string is positive
                                c=c.add(literal);
                            }
                            if (c == null){
                                c = new Clause();
                            }
                        }
                        f=f.addClause(c); //add the clauses to the formula
                    }
                    for (Clause c: f.getClauses()){
                        if (c == null){
                            throw new IOException("UNSATISFIABLE");
                        }
                        Iterator<Literal> it;
                        it = c.iterator();
                        if (it.hasNext()){
                            Literal firstItem = it.next();
                            Literal secondItem = it.hasNext() ? it.next() : firstItem;
                            graph.addEdge(firstItem.getNegation(), secondItem);
                            graph.addEdge(secondItem.getNegation(), firstItem);
                        }
                    }
                }
            } else {
                throw new IOException("invalid format for CNF. no P found.");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e){
            System.err.println("the CNF file has more clauses than included.");
        } finally {
            bin.close();
        }
        return f;
    }

    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }

    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
}
