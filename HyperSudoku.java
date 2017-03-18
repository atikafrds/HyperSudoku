import java.io.*;
import java.util.*;

public class HyperSudoku {

	static int[][] sudoku = new int[9][9];
	static int[][] solvedSudoku = new int[9][9];
	static Stack cellDone = new Stack();
	static int assignments = 0; // inisialisasi jumlah assignments
	static long startingTime, endTime;

	/* Tipe bentukan untuk menyimpan cell sebelumnya yang telah diisi */
	public static class Element {
		public int row;
		public int col;
		public Element(int row, int col) {
			this.row = row; // indeks baris
			this.col = col; // indeks kolom
		}
	}

	public static boolean check(int[][] input, int row, int col, int number) {
		int i, j, r, c;
		/* Memeriksa availability pada baris */
		boolean rowCheck = true;
		c = 0;
		while ((c < 9) && (rowCheck)) {
			if (input[row][c] == number) {
				rowCheck = false;
			}
			c++;
		}
		/* Memeriksa availability pada kolom */
		boolean colCheck = true;
		r = 0;
		while ((r < 9) && (colCheck)) {
			if (input[r][col] == number) {
				colCheck = false;
			}
			r++;
		}
		/* Memeriksa availability pada kotak */
		boolean boxCheck = true;
		r = (row / 3) * 3;
		c = (col / 3) * 3;
		i = 0;
		j = 0;
		while ((i < 3) && (boxCheck)) {
			while ((j < 3) && (boxCheck)) {
				if (input[r+i][c+j] == number) {
					boxCheck = false;
				}
				j++;
			}
			if (j == 3) {
				j = 0;
				i++;
			}
		}
		/* Memeriksa availability pada empat kotak di tengah */
		boolean hyperCheck = true;
		if ((1 <= row) && (row <= 3)) {
			if ((col >= 1) && (col <= 3)) {
				r = 1; c = 1;
				while ((r <= 3) && (hyperCheck)) {
					while ((c <= 3) && (hyperCheck)) {
						if (input[r][c] == number) {
							hyperCheck = false;
						}
						c++;
					}
					if (c > 3) {
						c = 1;
						r++;
					}
				}
			} else if ((col >= 5) && (col <= 7)) {
				r = 1; c = 5;
				while ((r <= 3) && (hyperCheck)) {
					while ((c <= 7) && (hyperCheck)) {
						if (input[r][c] == number) {
							hyperCheck = false;
						}
						c++;
					}
					if (c > 7) {
						c = 5;
						r++;
					}
				}
			}
		} else if ((5 <= row) && (row <= 7)) {
			if ((1 <= col) && (col <= 3)) {
				r = 5; c = 1;
				while ((r <= 7) && (hyperCheck)) {
					while ((c <= 3) && (hyperCheck)) {
						if (input[r][c] == number) {
							hyperCheck = false;
						}
						c++;
					}
					if (c > 3) {
						c = 1;
						r++;
					}
				}
			} else if ((5 <= col) && (col <= 7)) {
				r = 5; c = 5;
				while ((r <= 7) && (hyperCheck)) {
					while ((c <= 7) && (hyperCheck)) {
						if (input[r][c] == number) {
							hyperCheck = false;
						}
						c++;
					}
					if (c > 7) {
						c = 5;
						r++;
					}
				}
			} else {

			}
		}
		return (rowCheck && colCheck && boxCheck && hyperCheck);
	}
	
	/* Mencetak sudoku ke layar */
	public static void PrintSudoku(int[][] sudoku) {
		for (int row = 0; row < 9; row++) {
			if (row % 3 == 0) {
				System.out.println(" ----------------------- ");
			}
			for (int col = 0; col < 9; col++) {
				if (col % 3 == 0) {
					System.out.print("| ");
				}
				if (sudoku[row][col] == 0) {
					System.out.print(" ");
				} else {
					System.out.print(sudoku[row][col]);
				}
				System.out.print(" ");
			}
			System.out.println("| ");
		}
		System.out.println(" ----------------------- ");
	}

	/* Membaca input persoalan sudoku dari file teks */
	public static void ReadFile() {
		try {
			// FileReader membaca dari file eksternal bernama input.txt
			FileReader inputFile = new FileReader("input.txt");
			BufferedReader bufferReader = new BufferedReader(inputFile);
			String line = null;
			int row = 0;
			// Membaca file teks per baris
			while ((line = bufferReader.readLine()) != null) {
				String[] words = line.split(" ");
				for (int col = 0; col < words.length; col++) {
					int a = Integer.parseInt(words[col]);
					sudoku[row][col] = a; // menyimpan ke dalam array sudoku
					solvedSudoku[row][col] = a; // menyimpan ke dalam array solvedSudoku
				}
				row++;
			}
			// Menutup file eksternal
			bufferReader.close();
		} catch(Exception e) {
			System.out.println("Error while reading file line by line:" + e.getMessage());
		}
	}

	public static void SolveSudoku() {
		startingTime = System.nanoTime(); // Waktu awal program berjalan
		int i, j, k;
		i = 0;
		j = 0;
		while (i < 9) { // iterasi pada indeks baris
			if (sudoku[i][j] == 0) { // nilai nol menunjukkan cell yang kosong pada sudoku
				k = solvedSudoku[i][j] + 1;
				boolean available = false;
				while ((!available) && (k <= 9)) {
					if (check(solvedSudoku, i, j, k)) {
						available = true;
					} else {
						k++;
					}
					assignments++; // increment assignment, satu kali usaha untuk mengisi sebuah cell
				}
				if (check(solvedSudoku, i, j, k) && (k <= 9)) {
					solvedSudoku[i][j] = k; // mengisi cell dengan angka yang available
					cellDone.push(new Element(i, j)); // menyimpan indeks cell yang telah diisi ke dalam stack
					j++;
					if (j == 9) {
						j = 0;
						i++;
					}
				} else { // semua kemungkinan angka tidak available, melakukan backtrack
					solvedSudoku[i][j] = 0;
					try {
						// pop dari stack, menuju cell yang paling terakhir diisi
						Element temp = (Element) cellDone.pop();
						i = temp.row;
						j = temp.col;
					}
					catch (EmptyStackException e) { // backtrack dilakukan dan tidak ada solusi
						System.out.println("Sudoku tidak dapat diselesaikan.");
						break;
					}
				}
			} else {
				j++;
				if (j == 9) {
					j = 0;
					i++;
				}
			}
		}
		endTime = System.nanoTime(); // pencatatan waktu ketika program berakhir
	}

	public static void main(String[] args) {
		ReadFile();
		System.out.println(" ----------------------- ");
		System.out.println("   HYPER SUDOKU SOLVER   ");
		System.out.println(" ----------------------- ");
		System.out.println();
		System.out.println("Initial Sudoku");
		PrintSudoku(sudoku);
		System.out.println();
		SolveSudoku();
		System.out.println("Solved Sudoku");
		PrintSudoku(solvedSudoku);
		System.out.println();
		System.out.println("Jumlah assignment = " + assignments + " kali");
		System.out.println("Waktu eksekusi = " + ((endTime-startingTime)/1000000) + " ms");
	}
}