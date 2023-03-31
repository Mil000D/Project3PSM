/**
 * @author Miłosz Demendecki s24611
 */

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * I have created an example in Excel with chart and data copied from this program
 */
public class MidpointOrRK4 extends JFrame{
    private static final int g = -10;
    private static boolean inRadians = false;
    
    public static void main(String[] args) {
        createFrame();
    }

    /**
     * Function that adds records from list specified as parameter to text area,
     * so user can easily copy records and paste it to e.g. excel
     */
    public static void addListToArea(ArrayList<Double> list, String str, JTextArea textArea, JButton button, int rows){
        for (int i = 0; i < rows; i++) {
            textArea.append(list.get(i).toString().replace(".",",") + "\n");
            button.setText(str);
        }
    }

    /**
     * Method that computes KOmega, firstly by calculating _alpha for specified kAlpha
     * and then adding it formula for kNOmega or epsilon = g / l * Math.sin(_alpha) and at the end by returning its value
     */
    private static double computeKOmega(double kAlpha, double l, double alpha, double dt) {
        double _alpha = alpha + kAlpha * dt / 2;
        return g / l * Math.sin(_alpha);
    }

    /**
     * Method that computes KAlpha for specified parameters
     */
    private static double computeKAlpha(double kOmega, double omega, double dt) {
        return omega + kOmega * dt / 2;
    }

    /**
     * Constructor that takes multiple parameters to calculate all values needed for x, y, Ek, Ep and Et to be computed
     * and adds them to corresponding lists. After that correlated text areas and buttons are added to help user to utilize
     * calculated data easily.
     */
    public MidpointOrRK4(int rows, double l, double alpha, double mass, double omega, double dt, boolean midpoint) {
        ArrayList<Double> xList = new ArrayList<>();
        ArrayList<Double> yList = new ArrayList<>();
        ArrayList<Double> ekList = new ArrayList<>();
        ArrayList<Double> epList = new ArrayList<>();
        ArrayList<Double> etList = new ArrayList<>();

        if(!inRadians) {
            alpha = Math.toRadians(alpha);
        }

        for (int i = 0; i < rows; i++) {
            double x = l * Math.cos(alpha - Math.toRadians(90));
            xList.add(x);

            double y = l * Math.sin(alpha - Math.toRadians(90)) + l ;
            yList.add(y);

            double ek = mass * Math.pow(l * omega, 2) / 2;
            ekList.add(ek);

            double ep = Math.abs(mass * g * y);
            epList.add(ep);

            double et = ek + ep;
            etList.add(et);

            double epsilon = g / l * Math.sin(alpha);

            double dAlpha;
            double dOmega;

            if(midpoint) {
                double alpha_2 = alpha + omega * dt / 2;
                double omega_2 = omega + epsilon * dt / 2;
                double epsilon_2 = g / l * Math.sin(alpha_2);

                dAlpha = omega_2 * dt;
                dOmega = epsilon_2 * dt;
            } else {
                double k1Alpha = omega;
                double k1Omega = epsilon;
                double k2Alpha = computeKAlpha(k1Omega, omega, dt);
                double k2Omega = computeKOmega(k1Alpha, l, alpha, dt);
                double k3Alpha = computeKAlpha(k2Omega, omega, dt);
                double k3Omega = computeKOmega(k2Alpha, l, alpha, dt);
                double k4Alpha = computeKAlpha(k3Omega, omega, dt);
                double k4Omega = computeKOmega(k3Alpha, l, alpha, dt);

                dAlpha = (k1Alpha + 2 * k2Alpha + 2 * k3Alpha + k4Alpha) / 6 * dt;
                dOmega = (k1Omega + 2 * k2Omega + 2 * k3Omega + k4Omega) / 6  * dt;
            }
            alpha += dAlpha;
            omega += dOmega;
        }

        if(midpoint) {
            setTitle("Midpoint");
        } else {
            setTitle("Runge–Kutta");
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 300);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        for (int i = 1; i <= 5; i++) {
            JTextArea textArea = new JTextArea();
            JButton copyButton = new JButton();
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            textArea.setEditable(false);
            switch (i) {
                case 1 -> addListToArea(xList,"Copy x", textArea, copyButton, rows);
                case 2 -> addListToArea(yList,"Copy y", textArea, copyButton, rows);
                case 3 -> addListToArea(ekList,"Copy Ek", textArea, copyButton, rows);
                case 4 -> addListToArea(epList,"Copy Ep", textArea, copyButton, rows);
                case 5 -> addListToArea(etList,"Copy Et", textArea, copyButton, rows);
            }
            copyButton.addActionListener(e -> {
                textArea.selectAll();
                textArea.copy();
            });
            JPanel scrollPanel = new JPanel(new BorderLayout());
            scrollPanel.add(copyButton, BorderLayout.NORTH);
            scrollPanel.add(scrollPane, BorderLayout.CENTER);
            panel.add(scrollPanel);
        }
        getContentPane().add(panel);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    /**
     * Function that creates user interface
     */
    public static void createFrame(){
        JFrame startFrame = new JFrame("Choose Calculation");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label1 = new JLabel("Provide number of rows:");
        JTextField textField = new JTextField();

        JLabel label2 = new JLabel("Provide l value:");
        JTextField lField = new JTextField();

        JLabel label3 = new JLabel("Provide α value in degrees:");
        JTextField alphaField = new JTextField();

        JLabel label4 = new JLabel("Provide mass of an object:");
        JTextField massField = new JTextField();

        JLabel label5 = new JLabel("Provide ω value:");
        JTextField omegaField = new JTextField();

        JLabel label6 = new JLabel("Provide Δt value:");
        JTextField deltaTField = new JTextField();

        JButton calculateButton = new JButton("Calculate using Midpoint method");
        calculateButton.addActionListener(e -> {
            try {
                new MidpointOrRK4(Integer.parseInt(textField.getText()), Double.parseDouble(lField.getText()),
                        Double.parseDouble(alphaField.getText()),Double.parseDouble(massField.getText()),
                        Double.parseDouble(omegaField.getText()),Double.parseDouble(deltaTField.getText()), true);
            } catch (NumberFormatException ex){
                System.out.println("Try again, remember to pass all arguments");
            }
        });

        JButton calculateButton2 = new JButton("Calculate using Runge–Kutta method");
        calculateButton2.addActionListener(e -> {
            try {
                new MidpointOrRK4(Integer.parseInt(textField.getText()), Double.parseDouble(lField.getText()),
                        Double.parseDouble(alphaField.getText()),Double.parseDouble(massField.getText()),
                        Double.parseDouble(omegaField.getText()),Double.parseDouble(deltaTField.getText()), false);
            } catch (NumberFormatException ex) {
                System.out.println("Try again, remember to pass all arguments");
            }
        });

        JButton changeToRadiansOrDegreesButton = new JButton("Click me to provide alpha in radians");
        changeToRadiansOrDegreesButton.addActionListener(e -> {
            if(inRadians) {
                inRadians = false;
                label3.setText("Provide α value in degrees:");
                changeToRadiansOrDegreesButton.setText("Click me to provide α in radians");
            } else {
                inRadians = true;
                label3.setText("Provide α value in radians:");
                changeToRadiansOrDegreesButton.setText("Click me to provide α in degrees");
            }
        });

        startFrame.setResizable(true);
        panel.add(label1);
        panel.add(textField);

        panel.add(label2);
        panel.add(lField);

        panel.add(label3);
        panel.add(alphaField);

        panel.add(label4);
        panel.add(massField);

        panel.add(label5);
        panel.add(omegaField);

        panel.add(label6);
        panel.add(deltaTField);

        panel.add(calculateButton);
        panel.add(calculateButton2);
        panel.add(changeToRadiansOrDegreesButton);

        startFrame.getContentPane().add(panel);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setLocationRelativeTo(null);
        startFrame.setVisible(true);
        startFrame.pack();
    }
}