/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//* $Id: NumericOp.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.expr;

import com.wisii.fov.datatypes.PercentBaseContext;
import com.wisii.fov.datatypes.Numeric;

/**
 * This class contains static methods to evaluate operations on Numeric
 * operands. If the operands are absolute numerics the result is computed
 * rigth away and a new absolute numeric is return. If one of the operands are
 * relative a n operation node is created with the operation and the operands.
 * The evaluation of the operation can then occur when getNumericValue() is
 * called.
 */
public class NumericOp {
    /**
     * Add the two operands and return a new Numeric representing the result.
     * @param op1 The first operand.
     * @param op2 The second operand.
     * @return A Numeric representing the result.
     * @throws PropertyException If the dimension of the operand is different
     * from the dimension of this Numeric.
     */
    public static Numeric addition(Numeric op1, Numeric op2) throws PropertyException {
        if (op1.isAbsolute() && op2.isAbsolute()) {
            return addition2(op1, op2, null);
        } else {
            return new RelativeNumericProperty(RelativeNumericProperty.ADDITION, op1, op2);
        }
    }

    public static Numeric addition2(Numeric op1, Numeric op2, PercentBaseContext context) throws PropertyException {
        if (op1.getDimension() != op2.getDimension()) {
            throw new PropertyException("addition方法的Dimension数值不相同");
        }
        return numeric(op1.getNumericValue(context) + op2.getNumericValue(context), op1.getDimension());
    }

    /**
     * Add the second operand from the first and return a new Numeric
     * representing the result.
     * @param op1 The first operand.
     * @param op2 The second operand.
     * @return A Numeric representing the result.
     * @throws PropertyException If the dimension of the operand is different
     * from the dimension of this Numeric.
     */
    public static Numeric subtraction(Numeric op1, Numeric op2) throws PropertyException {
        if (op1.isAbsolute() && op2.isAbsolute()) {
            return subtraction2(op1, op2, null);
        } else {
            return new RelativeNumericProperty(RelativeNumericProperty.SUBTRACTION, op1, op2);
        }
    }

    public static Numeric subtraction2(Numeric op1, Numeric op2, PercentBaseContext context) throws PropertyException {
        if (op1.getDimension() != op2.getDimension()) {
            throw new PropertyException("subtract方法的Dimension数值不相同");
        }
        return numeric(op1.getNumericValue(context) - op2.getNumericValue(context), op1.getDimension());
    }

    /**
     * Multiply the two operands and return a new Numeric representing the
     * result.
     * @param op1 The first operand.
     * @param op2 The second operand.
     * @return A Numeric representing the result.
     * @throws PropertyException If the dimension of the operand is different
     * from the dimension of this Numeric.
     */
    public static Numeric multiply(Numeric op1, Numeric op2) throws PropertyException {
        if (op1.isAbsolute() && op2.isAbsolute()) {
            return multiply2(op1, op2, null);
        } else {
            return new RelativeNumericProperty(RelativeNumericProperty.MULTIPLY, op1, op2);
        }
    }

    public static Numeric multiply2(Numeric op1, Numeric op2, PercentBaseContext context) throws PropertyException {
        return numeric(op1.getNumericValue(context) * op2.getNumericValue(context),
                       op1.getDimension() + op2.getDimension());
    }

    /**
     * Divide the second operand into the first and return a new
     * Numeric representing the
     * result.
     * @param op1 The first operand.
     * @param op2 The second operand.
     * @return A Numeric representing the result.
     * @throws PropertyException If the dimension of the operand is different
     * from the dimension of this Numeric.
     */
    public static Numeric divide(Numeric op1, Numeric op2) throws PropertyException {
        if (op1.isAbsolute() && op2.isAbsolute()) {
            return divide2(op1, op2, null);
        } else {
            return new RelativeNumericProperty(RelativeNumericProperty.DIVIDE, op1, op2);
        }
    }

    public static Numeric divide2(Numeric op1, Numeric op2, PercentBaseContext context) throws PropertyException {
        return numeric(op1.getNumericValue(context) / op2.getNumericValue(context),
                       op1.getDimension() - op2.getDimension());
    }

    /**
     * Return the remainder of a division of the two operand Numeric.
     * @param op1 The first operand.
     * @param op2 The second operand.
     * @return A new Numeric object representing the absolute value.
     */
    public static Numeric modulo(Numeric op1, Numeric op2) throws PropertyException {
        if (op1.isAbsolute() && op2.isAbsolute()) {
            return modulo2(op1, op2, null);
        } else {
            return new RelativeNumericProperty(RelativeNumericProperty.MODULO, op1, op2);
        }
    }

    public static Numeric modulo2(Numeric op1, Numeric op2, PercentBaseContext context) throws PropertyException {
        return numeric(op1.getNumericValue(context) % op2.getNumericValue(context), op1.getDimension());
    }

    /**
     * Return the absolute value of a Numeric.
     * @param op the operand.
     * @return a new Numeric object representing the absolute value of the operand.
     */
    public static Numeric abs(Numeric op) throws PropertyException {
        if (op.isAbsolute()) {
            return abs2(op, null);
        } else {
            return new RelativeNumericProperty(RelativeNumericProperty.ABS, op);
        }
    }

    public static Numeric abs2(Numeric op, PercentBaseContext context) throws PropertyException {
        return numeric(Math.abs(op.getNumericValue(context)), op.getDimension());
    }

    /**
     * Return the negation of a Numeric.
     * @param op the  operand.
     * @return a new Numeric object representing the negation of the operand.
     */
    public static Numeric negate(Numeric op) throws PropertyException {
        if (op.isAbsolute()) {
            return negate2(op, null);
        } else {
            return new RelativeNumericProperty(RelativeNumericProperty.NEGATE, op);
        }
    }

    public static Numeric negate2(Numeric op, PercentBaseContext context) throws PropertyException {
        return numeric(- op.getNumericValue(context), op.getDimension());
    }

    /**
     * Return the larger of the two Numerics.
     * @param op1 The first operand.
     * @param op2 The second operand.
     * @return a Numeric which is the maximum of the two operands.
     * @throws PropertyException if the dimensions or value types of the operands are different.
     */
    public static Numeric max(Numeric op1, Numeric op2) throws PropertyException {
        if (op1.isAbsolute() && op2.isAbsolute()) {
            return max2(op1, op2, null);
        } else {
            return new RelativeNumericProperty(RelativeNumericProperty.MAX, op1, op2);
        }
    }

    public static Numeric max2(Numeric op1, Numeric op2, PercentBaseContext context) throws PropertyException {
        if (op1.getDimension() != op2.getDimension()) {
            throw new PropertyException("max方法的Dimension数值不相同");
        }
        return op1.getNumericValue(context) > op2.getNumericValue(context) ? op1 : op2;
    }

    /**
     * Return the smaller of two Numerics.
     * @param op1 The first operand.
     * @param op2 The second operand.
     * @return a Numeric which is the minimum of the two operands.
     * @throws PropertyException if the dimensions or value types of the operands are different.
     */
    public static Numeric min(Numeric op1, Numeric op2) throws PropertyException {
        if (op1.isAbsolute() && op2.isAbsolute()) {
            return min2(op1, op2, null);
        } else {
            return new RelativeNumericProperty(RelativeNumericProperty.MIN, op1, op2);
        }
    }

    public static Numeric min2(Numeric op1, Numeric op2, PercentBaseContext context) throws PropertyException {
        if (op1.getDimension() != op2.getDimension()) {
            throw new PropertyException("min方法的Dimension数值不相同");
        }
        return op1.getNumericValue(context) <= op2.getNumericValue(context) ? op1 : op2;
    }

    /**
     * Create a new absolute numeric with the specified value and dimension.
     * @param value
     * @param dimension
     * @return a new absolute numeric.
     */
    private static Numeric numeric(double value, int dimension) {
        return new NumericProperty(value, dimension);
    }
}
