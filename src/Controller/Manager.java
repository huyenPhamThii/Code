/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import GUI.PuzzleGUI;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author HuyenPT
 */
public class Manager {

    private PuzzleGUI viewPuzzle;
    int size = 3;
    int moveCount = 0;
    int elapseCount = 0;
    JButton[][] matrix;
    runElapse r = new runElapse();
    boolean isWin = false;

    public Manager(PuzzleGUI viewPuzzle) {
        this.viewPuzzle = viewPuzzle;
        createButton(size);
        r.start();
        newGame();
        viewPuzzle.setResizable(false);
    }

    public void createButton(int size) {
        this.size = size;
        int count = 0;
        // giao diện kiểu lưới grid, khaongr cách các btn là 10
        GridLayout grid = new GridLayout(size, size, 10, 10);
        viewPuzzle.getPanelPuzzle().setPreferredSize(new Dimension(size * 60, size * 60));//đẹp
        viewPuzzle.getPanelPuzzle().setLayout(grid);

        //dùng mảng 2 chiều đẩy các btn lên
        matrix = new JButton[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                count++;
                JButton btn = new JButton(count + "");
                matrix[i][j] = btn;//add btn vào matrix để dễ điều khiển 
                viewPuzzle.getPanelPuzzle().add(btn);
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!isWin) {
                            if (checkMoveButton(btn)) {
                                moveButton(btn);
                                moveCount++;
                                viewPuzzle.getLblMove().setText("Move Count: " + moveCount);
                                checkWin();
                            }
                        }else{
                            JOptionPane.showMessageDialog(viewPuzzle, "Please press 'New game'", "Notice", 2);
                        }
                    }
                });
            }
        }
        matrix[size - 1][size - 1].setText("");//set btn cuối trống
        mixButton();
        viewPuzzle.pack();//frame co dãn đúng với panel
    }

    public void mixButton() {
        for (int k = 0; k < 10; k++) {
            Point p = getButtonEmpty(); //lấy tọa độ của ô trống
            int i = p.x;// i là hàng btn rỗng
            int j = p.y;//j là cột btn rỗng

            Random rd = new Random();//tạo rd random 4 so 0 1 2 3 
            int valueRandom = rd.nextInt(4);
            switch (valueRandom) {
                //ngang là j, dọc là i
                case 0: {//up, 
                    if (i > 0) {
                        //set giá trị của ô trên vào ô rỗng
                        matrix[i][j].setText(matrix[i - 1][j].getText());
                        //set ô trên thành rỗng
                        matrix[i - 1][j].setText("");
                        break;
                    }
                }
                case 1: {//down
                    if (i < size - 1) {
                        matrix[i][j].setText(matrix[i + 1][j].getText());
                        matrix[i + 1][j].setText("");
                        break;
                    }
                }
                case 2: {//left
                    if (j > 0) {
                        matrix[i][j].setText(matrix[i][j - 1].getText());
                        matrix[i][j - 1].setText("");
                        break;
                    }
                }
                case 3: {//right
                    if (j < size - 1) {
                        matrix[i][j].setText(matrix[i][j + 1].getText());
                        matrix[i][j + 1].setText("");
                        break;
                    }
                }
            }
        }
    }

    //Tìm btn rỗng để đổi vị trí với btn bên cạnh
    //Point return tọa độ của btn tìm thấy
    public Point getButtonEmpty() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j].getText().equals("")) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    //check btn click có thể di chuyển không
    public boolean checkMoveButton(JButton btn) {
        int i1 = 0, j1 = 0;
        //tọa độ của btn rỗng
        int x = getButtonEmpty().x;
        int y = getButtonEmpty().y;
        //lấy tọa độ của btn click trong matrix
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (btn.getText().equals(matrix[i][j].getText())) {
                    i1 = i;
                    j1 = j;
                    break;
                }
            }
        }
        //left and right
        if ((x == i1 && y == (j1 - 1)) || (x == i1 && y == j1 + 1)) {
            return true;
        }
        //up and down
        if ((x == i1 - 1 && y == j1) || (x == i1 + 1 && y == j1)) {
            return true;
        }
        return false;
    }

    //move btn
    public void moveButton(JButton btn) {
        int i = getButtonEmpty().x;
        int j = getButtonEmpty().y;
        matrix[i][j].setText(btn.getText());
        btn.setText("");
    }

    class runElapse extends Thread {

        @Override
        public void run() {
            while (true) {
                viewPuzzle.getLblElap().setText("Elapsed: " + elapseCount + "(sec)");
                elapseCount++;
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    //user click to newGame btn -> reset all
    public void newGame() {
        viewPuzzle.getBtnNew().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPuzzle.getPanelPuzzle().removeAll();//xóa tất cả vaf set lại
                size = Integer.parseInt(viewPuzzle.getCbxSize().getSelectedItem().toString().split("x")[0]);
                createButton(size);
                moveCount = 0;
                viewPuzzle.getLblMove().setText("Move Count: 0");
                elapseCount = 0;
                isWin = false;
            }
        });
    }

    public void checkWin() {
        boolean check = true;
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                count++;
                if (!(matrix[i][j].getText().equals(count + ""))) {
                    check = false;
                    break;
                }
                if (count == (size * size - 1)) {
                    break;
                }
            }
        }
        if (check) {
            isWin = true;
            r.stop();
            JOptionPane.showMessageDialog(viewPuzzle, "You won!!!", "Result", 1);
        }
    }
}
