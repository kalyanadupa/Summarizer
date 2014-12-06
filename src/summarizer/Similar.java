/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package summarizer;

/**
 *
 * @author Kalyan
 */
public interface Similar<T> {
    public double similarity(T other);
}