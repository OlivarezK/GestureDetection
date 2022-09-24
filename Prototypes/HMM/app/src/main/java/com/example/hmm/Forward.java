package com.example.hmm;
import static java.lang.System.*;

public class Forward {
    public static double compute(int obs[], int states[], double start_prob[], double trans_prob[][], double emiss_prob[][]) throws ArrayIndexOutOfBoundsException
    {
        double forward[][] = new double[obs.length][states.length];

        out.println("The observation sequence - ");
        for(int i=0; i<obs.length; i++)
        {
            out.print(obs[i]+" ");
        }
        out.println();

        // Initializing the Forward Matrix
        for(int state : states)
        {
            forward[0][state] = start_prob[state] * emiss_prob[state][obs[0]-1];
        }

        for(int i=1; i<obs.length; i++)
        {
            for(int state1 : states)
            {
                forward[i][state1] = 0;

                for(int state2 : states)
                {
                    forward[i][state1] += forward[i - 1][state2] * trans_prob[state2][state1];

                    // Forward Algorithm adds up every probability calculated, takes to the maximum.
                }
                forward[i][state1] *= emiss_prob[state1][obs[i] - 1];
            }
        }

        // To check the status of Forward Matrix.
        for(int i=0; i<obs.length; i++)
        {
            for(int j=0; j<states.length; j++)
            {
                out.println(forward[i][j]+"");
            }
            //out.println();
        }

        // Calculation of final likelihood probability.
        double prob = 0;
        for(int state: states)
        {
            prob += forward[obs.length - 1][state];
        }


        return prob;
    }
}
