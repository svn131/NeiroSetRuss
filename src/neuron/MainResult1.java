package neuron;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MainResult1 {
    static List<Neuron> vhodnieNeironi = List.of(new Neuron(), new Neuron());
    static List<Neuron> skrytieNeironi = List.of(new Neuron(), new Neuron());
    static Neuron vyhodnoiNeiron = new Neuron();

    public static void main(String[] args) throws IOException {
        Random rnd = new Random();
        Scanner scn = new Scanner(System.in);

        for (Neuron vhodnoiNeiron : vhodnieNeironi) {
            for (Neuron skrytieNeiron : skrytieNeironi) {
                vhodnoiNeiron.aksyon.put(skrytieNeiron, rnd.nextDouble(-0.5, 0.5));
            }
        }

        for (Neuron skrytieNeiron : skrytieNeironi) {
            skrytieNeiron.aksyon.put(vyhodnoiNeiron, rnd.nextDouble(-0.5, 0.5));
        }

        training("src/neuron/training1.txt");

        System.out.print("Est' li oruzhie? (vvedite da/net): ");
        String val1 = scn.next();
        System.out.print("Raznitsa urovnei men'she 2? (vvedite da/net): ");
        String val2 = scn.next();

        vhodnieNeironi.get(0).znachenie = val1.equalsIgnoreCase("da") ? 1 : 0;
        vhodnieNeironi.get(1).znachenie = val2.equalsIgnoreCase("da") ? 1 : 0;

        double res = calc();

        System.out.println("res = " + res);
        System.out.println(res > 0.5 ? "Atakuyem" : "Bezhim");
    }

    static void training(String trainingFilePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(trainingFilePath));
        for (int i = 0; i < 100; i++) {
            for (String line : lines) {
                String[] data = line.split(" ");
                int index = 0;
                for (int j = 0; j < vhodnieNeironi.size(); j++) {
                    vhodnieNeironi.get(j).znachenie = Double.valueOf(data[index++]);
                }
                double ozhidayemiyRezultat = Double.valueOf(data[index]);
                double obscheyeZnacheniye = calc();
                double rezultat = obscheyeZnacheniye > 0.5 ? 1 : 0;
                if (rezultat != ozhidayemiyRezultat) {
                    resolveWeights(obscheyeZnacheniye, ozhidayemiyRezultat);
                }
            }
        }
    }

    static void resolveWeights(double obscheyeZnacheniye, double ozhidayemoyeZnacheniye) {
        double oshibka = obscheyeZnacheniye - ozhidayemoyeZnacheniye;
        double delta = oshibka * (1 - oshibka);

        for (Neuron skrytieNeiron : skrytieNeironi) {
            Double stariyeVesa = skrytieNeiron.aksyon.get(vyhodnoiNeiron);
            skrytieNeiron.aksyon.put(vyhodnoiNeiron, stariyeVesa - skrytieNeiron.znachenie * delta * 0.3);
        }

        for (Neuron skrytieNeiron : skrytieNeironi) {
            double oshibka2 = skrytieNeiron.aksyon.get(vyhodnoiNeiron) * delta;
            double delta2 = oshibka2 * (1 - oshibka2);

            for (Neuron vhodnoiNeiron : vhodnieNeironi) {
                Double stariyeVesa = vhodnoiNeiron.aksyon.get(skrytieNeiron);
                vhodnoiNeiron.aksyon.put(skrytieNeiron, stariyeVesa - vhodnoiNeiron.znachenie * delta2 * 0.3);
            }
        }
    }

    static double calc() {
        for (Neuron skrytieNeiron : skrytieNeironi) {
            double suma = 0;
            for (Neuron vhodnoiNeiron : vhodnieNeironi) {
                suma += vhodnoiNeiron.znachenie * vhodnoiNeiron.aksyon.get(skrytieNeiron);
            }
            skrytieNeiron.znachenie = sigma(suma);
        }

        double suma = 0;
        for (Neuron skrytieNeiron : skrytieNeironi) {
            suma += skrytieNeiron.znachenie * skrytieNeiron.aksyon.get(vyhodnoiNeiron);
        }

        vyhodnoiNeiron.znachenie = sigma(suma);
        return vyhodnoiNeiron.znachenie;
    }

    static double sigma(double obscheyeVeseniye) {
        return 1 / (1 + Math.pow(Math.E, -obscheyeVeseniye));
    }
}