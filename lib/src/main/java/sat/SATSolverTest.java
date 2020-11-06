package sat;

import java.io.File;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Arrays;
import java.util.Scanner;

import sat.env.*;
import sat.formula.*;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();




    // TODO: add the main method that reads the .cnf file and calls SATSolver.solve to determine the satisfiability
    public static void main(String[] args) {
        String file = String.join("",args);
        System.out.println("Reading File");
        long readTime = System.nanoTime();
        File fin;
        Scanner bin = null;
        try {
//            String currentDirectory = System.getProperty("user.dir");
//            System.out.println("The current working directory is " + currentDirectory);
            fin=new File(file); //import file
            bin=new Scanner(fin);
//            System.out.println("bin here:" + bin.nextLine());

            String line;

            String[] format = new String[4];

            boolean commentCheck=true;
            while(commentCheck){
                String commentRemove=bin.nextLine();
                if(commentRemove.startsWith("p") || commentRemove.startsWith("P")){
                    format = commentRemove.split("\\s+");
                    commentCheck = false;
                }else{
                    commentCheck = true;
                }
            }
//            System.out.println("bin here: " + bin.toString());
//            System.out.println("format here: "  + Arrays.toString(format));
            int NumberOfClauses=Integer.parseInt(format[3]);//get the number of clauses
            Formula f = new Formula(); //create and instance of the formula
            while (f.getSize()!=NumberOfClauses) {

                line=bin.nextLine();
                if(line.length()>0){
                    String[] tempLine=line.trim().split("\\s+");
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
            }
            String fileName = "BoolAssignment.txt";
            PrintWriter write = new PrintWriter(fileName, "UTF-8");
            long endReadTime = System.nanoTime();
            long tReadTime = endReadTime - readTime;
            System.out.println("Reading Time: " + tReadTime/1000000000.0 + "s");
            System.out.println("SAT Solver starts");
            long started = System.nanoTime();
            Environment env = SATSolver.solve(f);
//            System.out.println("env: " + env);
            long time = System.nanoTime();
            long timeTaken = time - started;
            System.out.println("Solving Time: " + timeTaken/1000000.0 + "ms");
            System.out.println("Total Time: " + (timeTaken+tReadTime)/1000000000.0 + "s");
            if (env == null){
                System.out.println("Formula Unsatisfiable");
            }
            else{
                System.out.println("Formula Satisfiable");
                String bindings = env.toString();
//                System.out.println(bindings);
                bindings = bindings.substring(bindings.indexOf("[")+1, bindings.indexOf("]"));
                String[] bindingNew = bindings.split(", ");
                //if (bindingNew.length > 1) {
                for (String binding : bindingNew) {
                    String[] bind = binding.split("->");
                    write.println(bind[0] + ":" + bind[1]);
                }
                //}


            }
            write.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bin != null) {
                bin.close();

            }
        }

    }

    public void testSATSolver1(){
        // (a v b)
        Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);
/*
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())
    			|| Bool.TRUE == e.get(b.getVariable())	);

*/
    }


    public void testSATSolver2(){
        // (~a)
        Environment e = SATSolver.solve(makeFm(makeCl(na)));
/*
    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
*/
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