package UtilityClasses;

import androidx.annotation.NonNull;

public class Matrix {
	private double[][] data;
	private int height;
	private int width;
	
	public Matrix(int h, int w) {
		height = h;
		width = w;
		data = new double[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				data[i][j] = 0;
			}
		}
	}
	
	public Matrix(double[][] arr) {
		data = arr;
		height = data.length;
		width = data[0].length;
	}
	
	public int[] getDims() {
		int[] dims = { height, width };
		return dims;
	}
	
	public double get(int i, int j) {
		return data[i][j];
	}
	
	public void set(int i, int j, double element) {
		data[i][j] = element;
	}
	
	public double[][] getData() { return data; }
	
	public Matrix scale(double scalar) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				data[i][j] *= scalar;
			}
		}
		return this;
	}
	
	public Matrix add(Matrix matrix) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				data[i][j] += matrix.data[i][j];
			}
		}
		return this;
	}
	
	public Matrix sub(Matrix matrix) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				data[i][j] -= matrix.data[i][j];
			}
		}
		return this;
	}
	
	public Matrix elementMul(Matrix matrix) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				data[i][j] *= matrix.data[i][j];
			}
		}
		return this;
	}
	
	public Matrix elementDiv(Matrix matrix) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				data[i][j] /= matrix.data[i][j];
			}
		}
		return this;
	}
	
	public Matrix mul(Matrix matrix) {
		Matrix result = new Matrix(height, matrix.width);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < matrix.width; j++) {
				for (int k = 0; k < matrix.height; k++) {
					result.data[i][j] += data[i][k] * matrix.data[k][j];
				}
			}
		}
		data = result.data;
		height = result.height;
		width = result.width;
		return this;
	}
	
	@NonNull
	public Matrix clone() {
		return new Matrix(this.data);
	}
}
