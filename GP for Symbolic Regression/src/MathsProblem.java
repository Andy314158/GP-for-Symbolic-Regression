import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPProblem;
import org.jgap.gp.function.*;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;


public class MathsProblem extends GPProblem {

    private GPConfiguration config;
    private Variable vx;

     public MathsProblem(GPConfiguration config, Variable vx) throws InvalidConfigurationException {
        super(config);
        this.config = config;
        this.vx = vx;
    }

    @Override
    public GPGenotype create() throws InvalidConfigurationException {
        Class[] types = {CommandGene.DoubleClass};
        Class[][] argTypes = {{},};
        CommandGene[][] nodeSets = {{
                vx,
                new Pow(config, CommandGene.DoubleClass),
                new Multiply(config, CommandGene.DoubleClass),
                new Add(config, CommandGene.DoubleClass),
                new Divide(config, CommandGene.DoubleClass),
                new Subtract(config, CommandGene.DoubleClass),
                new Terminal(config, CommandGene.DoubleClass, 2.0d, 10.0d, true)
        }
        };

        return GPGenotype.randomInitialGenotype(config, types, argTypes, nodeSets, 20, true);
    }
}
