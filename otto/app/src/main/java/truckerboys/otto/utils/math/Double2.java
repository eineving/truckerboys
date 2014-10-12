package truckerboys.otto.utils.math;

/**
 * A two-dimensional double vector.
 *
 * Primary use is gps-coordinate interpolation, the class may be expanded if needed in the future.
 *
 * Created by Nilsson on 2014-10-05.
 */
public class Double2 {
    private double x, y;

    public Double2(){
        x = 0.0;
        y = 0.0;
    }

    public Double2(double x, double y){
        this.x = x;
        this.y = y;
    }


    /**
     * Returns a new Double2 that is the product of the vector and the argument.
     * @param rhs The value to multiply by.
     * @return The product.
     */
    public Double2 mul(double rhs){
        return new Double2(x * rhs, y * rhs);
    }

    /**
     * Divides a vector by the argument.
     * Dividision by zero results in a vector with NaN as values.
     * @param rhs The number to divide by.
     * @return The quotient.
     */
    public Double2 div(double rhs){
        return new Double2(x / rhs, y / rhs);
    }

    /**
     * Subtracts the argument from the vector.
     * @param rhs The vector to subtract.
     * @return The difference between the vectors.
     */
    public Double2 sub(Double2 rhs){
        return new Double2(x - rhs.getX(), y - rhs.getY());
    }

    /**
     * Adds a the argument to the vector.
     * @param rhs The vector to add.
     * @return The sum of the vectors.
     */
    public Double2 add(Double2 rhs){
        return new Double2(x + rhs.getX(), y + rhs.getY());
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

}
