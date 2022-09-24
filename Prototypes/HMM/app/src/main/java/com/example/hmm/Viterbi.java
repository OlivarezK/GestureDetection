package com.example.hmm;
import static java.lang.System.*;

public class Viterbi {
    public static int[] compute(int obs[], int states[], double start_prob[], double trans_prob[][], double emiss_prob[][]) throws ArrayIndexOutOfBoundsException
    {
        double[][] viterbi = new double[obs.length][states.length];
        int [][] path = new int[states.length][obs.length];

        out.println("The observation sequence -");
        for(int i=0; i<obs.length; i++)
        {
            out.print(obs[i] + " ");
        }

        // Viterbi matrix initializing.
        for(int state : states)
        {
            viterbi[0][state] = start_prob[state] * emiss_prob[state][obs[0] - 1];
            path[state][0] = state;
        }

        for(int i=1; i<obs.length; i++)
        {
            int [][] newpath = new int[states.length][obs.length];

            for(int cur_state : states)
            {
                double prob = -1.0;
                int state;
                for(int from_state: states)
                {
                    double nprob = viterbi[i - 1][from_state] * trans_prob[from_state][cur_state] * emiss_prob[cur_state][obs[i] - 1];
                    if(nprob > prob)
                    {
                        // Re-assign, if only greater.
                        prob = nprob;
                        state = from_state;
                        viterbi[i][cur_state] = prob;
                        System.arraycopy(path[state], 0, newpath[cur_state], 0, i);
                        newpath[cur_state][i] = cur_state;

                    }
                }
            }
            path = newpath;

        }

        // Construction of Viterbi matrix.

        for(int i =0; i<obs.length; i++)
        {
            for(int j=0; j<states.length; j++)
            {
                out.print(viterbi[i][j]+" ");
            }
            out.println();
        }

        double prob = -1;
        int state = 0;
        // The final path computataion.
        for(int state1: states)
        {
            if(viterbi[obs.length - 1][state1] > prob)
            {
                prob = viterbi[obs.length - 1][state1];
                state = state1;
            }

        }


        return path[state];
    }
}
