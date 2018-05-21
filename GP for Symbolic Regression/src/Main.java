import org.jgap.gp.CommandGene;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Variable;
import org.jgap.util.SystemKit;
import java.io.InputStreamReader;
import java.util.Scanner;


public class Main {

    public static final Object[] NO_ARGS = new Object[0];
    public static final double MIN_ER = 0.001; //minimal acceptance error
    public static final int MAX_EVO = 1000; //max evolutions
    public static final int INPUT_SIZE = 20;

    private double input[];
    private double output[];
    private Variable variable;
    private MathsProblem problem;
    private GPConfiguration config;

    /**
     * Start the algorithm. It starts by loading in the data.
     *
     * @param file
     */
    private Main(String file) {

        input = new double[INPUT_SIZE];
        output = new double[INPUT_SIZE];
        Scanner scan = new Scanner(new InputStreamReader(ClassLoader.getSystemResourceAsStream(file)));

        scan.nextLine();
        scan.nextLine();

        for (int i = 0; scan.hasNextDouble(); i++) {
            input[i] = scan.nextDouble();
            output[i] = scan.nextDouble();
        }
    }


    /**
     *
     * @throws Exception
     */
    private void initConfig() throws Exception {
        config = new GPConfiguration(); //create config
        config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());//compute a defect rate
        config.setFitnessFunction(new MathsFitnessFunction());//assign out fitness function

        // assign properties
        config.setMaxCrossoverDepth(8);
        config.setMaxInitDepth(4);
        config.setPopulationSize(1000);

        config.setStrictProgramCreation(true);
        config.setReproductionProb(0.2f);
        config.setCrossoverProb(0.9f);
        config.setMutationProb(35.0f);

        variable = Variable.create(config, "X", CommandGene.DoubleClass);

        problem = new MathsProblem(config, variable);
    }


    /**
     * Get the best solution by evolution
     *
     * @throws Exception
     */
    private void run() throws Exception {

        GPGenotype gp = problem.create();
        gp.setGPConfiguration(config);
        gp.setVerboseOutput(true);
        evolve(gp);
        gp.outputSolution(gp.getAllTimeBest());
        problem.showTree(gp.getAllTimeBest(), "best-solution.png");

    }


    private void evolve(GPGenotype program) {
        int offset = program.getGPConfiguration().getGenerationNr();
        int c=0;
        for (int i = 0; i < MAX_EVO; ++i) {
            program.evolve();
            program.calcFitness();
            double fitness = program.getAllTimeBest().getFitnessValue();

            if (fitness < MIN_ER) {
                break;
            }
            if (i % 25 == 0) {
                String freeMB = SystemKit.niceMemory(SystemKit.getFreeMemoryMB());
                System.out.println("Evolving gen " + (i + offset) + freeMB + " MB, " +
                        "Current fittest program: " + fitness);
            }
            c=i;
        }
        System.out.println("After " + c + " evolutions the program had a fitness of: " + program.getAllTimeBest().getFitnessValue());
    }


    /**
     * Main entry point of the program
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // Check valid usage
        if (args.length != 1) {
            System.out.println("Invalid usage. \nArguments: Filename");
            return;
        }

        // Create application and run
        Main main = new Main(args[0]);
        main.initConfig();
        main.run();
    }



    /**
     * Fitness function
     */
    public class MathsFitnessFunction extends GPFitnessFunction {

        @Override
        protected double evaluate(IGPProgram igpProgram) {
            double totalError = 0;

            for (int i = 0; i < Main.INPUT_SIZE; i++) {
                variable.set(input[i]);

                double result = igpProgram.execute_double(0, NO_ARGS);
                totalError += Math.abs(result - output[i]);

                if (Double.isInfinite(totalError)) {
                    return Double.MAX_VALUE;
                }
            }

            if (totalError < MIN_ER) {
                return 0;
            }
            return totalError;
        }
    }




}


