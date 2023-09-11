package neuron;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MainResult2 {
    static List<Neuron> inputNeurons = List.of(new Neuron(), new Neuron(), new Neuron());
    static List<Neuron> hideNeurons = List.of(new Neuron(), new Neuron());
    static Neuron outputNeuron = new Neuron();
    public static void main(String[] args) throws IOException {
        Random rnd = new Random();
        Scanner scn = new Scanner(System.in);

        for (Neuron inputNeuron : inputNeurons) {
            for (Neuron hideNeuron : hideNeurons) {
                inputNeuron.axons.put(hideNeuron, rnd.nextDouble(-0.5, 0.5));
            }
        }

        for (Neuron hideNeuron : hideNeurons) {
            hideNeuron.axons.put(outputNeuron, rnd.nextDouble(-0.5, 0.5));
        }
        training("src/neuron/training2.txt");

        System.out.print("Есть ли оружие? (введите да/нет): ");
        String val1 = scn.next();
        System.out.print("Разница уровней меньше 2? (введите да/нет): ");
        String val2 = scn.next();
        System.out.print("Нас двое? (введите да/нет): ");
        String val3 = scn.next();

        inputNeurons.get(0).value = val1.equalsIgnoreCase("да")?1:0;
        inputNeurons.get(1).value = val2.equalsIgnoreCase("да")?1:0;
        inputNeurons.get(2).value = val3.equalsIgnoreCase("да")?1:0;


        double res = calc();

        System.out.println("res = "+res);
        System.out.println(res>0.5?"Атакуем":"Бежим");


    }
    //Наличие оружия
    //Разница уровней меньше 2
    static void training(String trainingFilePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(trainingFilePath));
        for (int i = 0; i < 100; i++) {


            for (String line : lines) {
                String[] data = line.split(" ");
                int index = 0;
                for (int j = 0; j < inputNeurons.size(); j++) {
                    inputNeurons.get(j).value = Double.valueOf(data[index++]);
                }
                double expectedResult = Double.valueOf(data[index]);
                double totalValue = calc();
                double result = totalValue > 0.5 ? 1 : 0;
                if (result != expectedResult) {
                    resolveWeights(totalValue, expectedResult);
                }
            }
        }
    }

    static void training2() throws IOException {





            double expectedResult = 0;
            double totalValue = calc();
            double result = totalValue > 0.5 ? 1 : 0;
            if (result != expectedResult) {
                resolveWeights(totalValue, expectedResult);
            }


    }


    static void resolveWeights(double totalValue, double expectedValue){
        double error = totalValue-expectedValue;
        double delta = error*(1-error);
        for (Neuron hideNeuron : hideNeurons) {
            Double oldWeight = hideNeuron.axons.get(outputNeuron);
            hideNeuron.axons.put(outputNeuron, oldWeight - hideNeuron.value*delta*0.3);
        }
        for (Neuron hideNeuron : hideNeurons) {
            double error2 = hideNeuron.axons.get(outputNeuron) * delta;
            double delta2 = error2*(1-error2);
            for (Neuron inputNeuron : inputNeurons) {
                Double oldWeight = inputNeuron.axons.get(hideNeuron);
                inputNeuron.axons.put(hideNeuron, oldWeight - inputNeuron.value*delta2*0.3);
            }
        }

    }

    static double calc(){

        for (Neuron hideNeuron : hideNeurons) {
            double sum = 0;
            for (Neuron inputNeuron : inputNeurons) {
                sum+=inputNeuron.value*inputNeuron.axons.get(hideNeuron);
            }
            hideNeuron.value = sigma(sum);
        }
        double sum = 0;
        for (Neuron hideNeuron : hideNeurons) {
            sum+=hideNeuron.value*hideNeuron.axons.get(outputNeuron);
        }
        outputNeuron.value = sigma(sum);
        return outputNeuron.value;
    }

    static double sigma(double totalWeight){
        return 1/(1+Math.pow(Math.E, -totalWeight));
    }



}

