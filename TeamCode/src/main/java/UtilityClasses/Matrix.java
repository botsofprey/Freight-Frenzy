package UtilityClasses;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Objects;

@Deprecated
public class Matrix {
	private double[][] data;
	private int height;
	private int width;
	
	public Matrix(int h, int w, double scale) {
		height = h;
		width = w;
		data = new double[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				data[i][j] = i == j ? scale : 0;
			}
		}
	}
	
	public Matrix(int h, int w) {
		this(h, w, 1);
	}
	
	public Matrix(double[][] arr) {
		data = arr;
		height = data.length;
		width = data[0].length;
	}
	
	public int[] getDims() {
		return new int[]{ height, width };
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
		Matrix result = new Matrix(height, matrix.width, 0);
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
	
	public Matrix transpose() {
		double[][] newData = new double[width][height];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				newData[j][i] = data[i][j];
			}
		}
		data = newData;
		int temp = width;
		width = (height);
		height = temp;
		return this;
	}
	
	@NonNull
	public Matrix clone() {
		return new Matrix(this.data);
	}
	
	@Override
	public String toString() {
		return "Matrix{" +
				"width=" + width +
				", height=" + height +
				", data=" + getDataAsString() +
				'}';
	}
	
	private String getDataAsString() {
		StringBuilder string = new StringBuilder("[" + Arrays.toString(data[0]));
		for (int i = 1; i < data.length; i++) {
			string.append(",\n").append(Arrays.toString(data[i]));
		}
		string.append("]");
		return string.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Matrix matrix = (Matrix) o;
		return height == matrix.height && width == matrix.width && Arrays.equals(data, matrix.data);
	}
	
	@Override
	public int hashCode() {
		int result = Objects.hash(height, width);
		result = 31 * result + Arrays.hashCode(data);
		return result;
	}
}
