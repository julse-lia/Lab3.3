package com.example.lab_33;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final static Random RANDOM = new Random();
    private final static int N = 4;
    private static int resultGenotype;
    private int a, b, c, d, y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View v) {
        String inputA = ((EditText) findViewById(R.id.a)).getText().toString();
        String inputB = ((EditText) findViewById(R.id.b)).getText().toString();
        String inputC = ((EditText) findViewById(R.id.c)).getText().toString();
        String inputD = ((EditText) findViewById(R.id.d)).getText().toString();
        String inputY = ((EditText) findViewById(R.id.y)).getText().toString();

        if (inputA.isEmpty() || inputB.isEmpty() || inputC.isEmpty() || inputD.isEmpty() || inputY.isEmpty()) {
            Toast.makeText(this, "Введіть всі значення рівняння!", Toast.LENGTH_SHORT).show();
            return;
        }
        a = Integer.parseInt(inputA);
        b = Integer.parseInt(inputB);
        c = Integer.parseInt(inputC);
        d = Integer.parseInt(inputD);
        y = Integer.parseInt(inputY);

        int f;
        int[] deltas = new int[N];
        int[][] population = generatePopulation();
        long time = System.nanoTime();
        boolean flag = true;
        while (flag) {
            for (int i = 0; i < N; i++) {
                f = a * population[i][0] + b * population[i][1] + c * population[i][2] + d * population[i][3];
                deltas[i] = Math.abs(y - f);
            }
            for (int i = 0; i < N; i++) {
                if (deltas[i] == 0) {
                    resultGenotype = i;
                    flag = false;
                    break;
                }
            }
            if (!flag) break;
            population = newGeneration(population, generateProbabilities(deltas));
        }
        time = System.nanoTime() - time;
        TextView result = findViewById(R.id.result);
        result.setText("Корені: " + Arrays.toString(population[resultGenotype]) + "\nЧас(сек): " + time / 1_000_000_000.0);
    }

    private int[][] generatePopulation() {
        int[][] population = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                population[i][j] = RANDOM.nextInt(y);
            }
        }
        return population;
    }

    private double[] generateProbabilities(int[] deltas) {
        double sum = 0.0;
        double[] probabilities = new double[N];
        for (int i = 0; i < N; i++) {
            probabilities[i] = 1.0 / deltas[i];
            sum += probabilities[i];
        }
        for (int i = 0; i < N; i++) {
            probabilities[i] /= sum;
        }
        for (int i = 1; i < N; i++) {
            probabilities[i] += probabilities[i-1];
        }
        System.out.println(Arrays.toString(probabilities));
        return probabilities;
    }

    private int[][] newGeneration(int[][] oldPopulation, double[] probabilities) {
        int[][] newGen = new int[N][N];
        for (int i = 0; i < N; i++) {
            int root1 = peekRoot(probabilities);
            int root2 = peekRoot(probabilities);
            newGen[i][0] = oldPopulation[root1][0];
            newGen[i][1] = oldPopulation[root1][1];
            newGen[i][2] = oldPopulation[root2][2];
            newGen[i][3] = oldPopulation[root2][3];
        }
        generateMutation(newGen);
        return newGen;
    }
    private int[][] generateMutation(int[][] population){
        double mutationProbabilities = 0.1;
        int numberOfMutation = (int) Math.round(mutationProbabilities*N*N);

        for (int i = 0; i < numberOfMutation; i++) {
            population[RANDOM.nextInt(N)][RANDOM.nextInt(N)] = RANDOM.nextInt(y);
        }
        return population;
    }

    private int peekRoot(double[] probabilities) {
        double rand = RANDOM.nextDouble();
        if(rand < probabilities[0]) {
            return 0;
        } else if (rand < probabilities[1]) {
            return 1;
        } else if (rand < probabilities[2]) {
            return 2;
        } else {
            return 3;
        }
    }
}