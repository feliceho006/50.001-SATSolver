package sat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        boolean hasP = false;
        Clause[] clauses = null;
        int clausePointer = 0;
        File fin;
        Scanner bin = null;
        Formula f = new Formula(); //create and instance of the formula

        try {
            fin = new File(filename);
            bin = new Scanner(fin);
            String line;

            String[] format = new String[4];

            boolean commentCheck=true;
            while(commentCheck){
                String commentRemove=bin.nextLine();
                if(commentRemove.startsWith("p") || commentRemove.startsWith("P")){
                    format = commentRemove.split("\\s+");
                    commentCheck = false;
                    break;
                }else{
                    commentCheck = true;
                }
            }
            int NumberOfClauses=Integer.parseInt(format[3]);//get the number of clauses


            if (commentCheck == false){
                bin.useDelimiter(" 0");
                while (f.getSize()!=NumberOfClauses) {
                    line=bin.nextLine();
                    if(line.length()>0){
                        String[] tempLine=line.trim().split("\\s+");
                        if (tempLine.length >2){
                            is2SAT = false;
                        }
//                    System.out.println(Arrays.toString(tempLine));
                        Clause c = new Clause();

                        for(String i:tempLine){

                            if(Integer.parseInt(i)==0){
                                break;
                            }

//                        System.out.println(i);

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
//                    System.out.println("f is here: " + f.toString());
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
              /*--------------------------------------------------------------------------**/
//                while (f.getSize()!=NumberOfClauses){
//                    String next = bin.next();
//                    String[] values = next.trim().split(" ");
//                    if (values.length > 2){
//                        // throw new IOException("The .cnf file has more than 2 literals in a clause.");
//                        is2SAT = false;
//                    }
//                    Literal[] literals = new Literal[values.length];
//                    for (int i = 0; i < literals.length; i++){
//                        String temp = values[i].trim();
//                        if (temp.length() > 0)
//                            literals[i] = temp.charAt(0) == '-' ? NegLiteral.make(temp.substring(1)) : PosLiteral.make(temp);
//                    }


//                    if (literals[0] != null){
//                        clauses[clausePointer] = makeCl(literals);
//                        // if there is a negation of itself in the same clause
//                        if (clauses[clausePointer] == null)
//                            throw new IOException("UNSATISFIABLE");

                        // based on the current clause, construct the directed graph.
//                        Iterator<Literal> it;
//                        it = clauses[clausePointer].iterator();
//                        if (it.hasNext()){
//                            Literal firstItem = it.next();
//                            Literal secondItem = it.hasNext() ? it.next() : firstItem;
//                            graph.addEdge(firstItem.getNegation(), secondItem);
//                            graph.addEdge(secondItem.getNegation(), firstItem);
//                        }
//                        clausePointer--;
//                    }
//
//
//                }
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
