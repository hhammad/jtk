/****************************************************************************
Copyright (c) 2004, Colorado School of Mines and others. All rights reserved.
This program and accompanying materials are made available under the terms of
the Common Public License - v1.0, which accompanies this distribution, and is
available at http://www.eclipse.org/legal/cpl-v10.html
****************************************************************************/
package edu.mines.jtk.util;

import static java.lang.Math.*;

/**
 * Tics for annotating an axis. Given values at the endpoints of the axis,
 * axes tics are constructed by computing parameters for both major and
 * minor tics. Major tics are a subset of minor tics. Typically, major 
 * tics are labeled with character strings that represent their values. 
 * <p>
 * Axes tics can be constructed in two ways, by specifying either (1) the 
 * interval between major tics or (2) the maximum number of major tics.
 * <p>
 * In the first case, when the major tic interval (a positive number) is 
 * specified, other tic parameters are easily computed. For example, the 
 * value of the first major tic equals the smallest multiple of the major 
 * tic interval that is not less than the minimum of the axis endpoint 
 * values. Likewise, the number of major tics is computed so that the 
 * value of the last major tic is not greater than the maximum of the 
 * axis endpoint values.
 * <p>
 * In the second case, when the maximum number of major tics is specified, 
 * the major tic interval is computed to be 2, 5, or 10 times some power 
 * of 10. Then, other tic parameters are computed as in the first case. 
 * The tricky part in this second case is choosing the best number from 
 * the set {2,5,10}. That best number is called the tic <em>multiple</em>, 
 * and is computed so that the number of major tics is close to, but not 
 * greater than, the specified maximum number of major tics.
 * <p>
 * After construction, the counts, increments, and first values of both 
 * major and minor tics are available.
 * @author Dave Hale, Colorado School of Mines
 * @version 2004.12.14
 */
public class AxisTics {

  /**
   * Constructs axis tics for a specified major tic interval.
   * @param x1 the value at one end of the axis.
   * @param x2 the value at the other end of the axis.
   * @param dtic the major tic interval; a positive number.
   */
  public AxisTics(double x1, double x2, double dtic) {
    double xmin = min(x1,x2);
    double xmax = max(x1,x2);
    double d = abs(dtic);
    double f = ((int)(xmin/d)-1)*d;
    while (f<xmin)
      f += d;
    int n = 1+(int)((xmax-f)/d);
    _xmin = xmin;
    _xmax = xmax;
    _ntic = n;
    _dtic = d;
    _ftic = f;
    computeMultiple();
    computeMinorTics();
  }

  /**
   * Constructs axis tics for a specified maximum number of major tics.
   * @param x1 the value at one end of the axis.
   * @param x2 the value at the other end of the axis.
   * @param ntic the maximum number of major tics.
   */
  public AxisTics(double x1, double x2, int ntic) {
    double xmin = min(x1,x2);
    double xmax = max(x1,x2);
    if (ntic<=0)
      ntic = 1;
    int nmult = _mult.length;
    int nbest = 0;
    int mbest = 0;
    double dbest = 0.0;
    double fbest = 0.0;
    for (int imult=0; imult<nmult; ++imult) {
      int m = _mult[imult];
      int l = (int)(log10((xmax-xmin)/(m*ntic)));
      double d = m*pow(10.0,l);
      double f = ((int)(xmin/d)-1)*d;
      while (f<xmin)
        f += d;
      int n = 1+(int)((xmax-f)/d);
      if (nbest<n && n<=ntic) {
        nbest = n;
        mbest = m;
        dbest = d;
        fbest = f;
      }
    }
    if (mbest==1)
      mbest = 10;
    _xmin = xmin;
    _xmax = xmax;
    _mtic = mbest;
    _ntic = nbest;
    _dtic = dbest;
    _ftic = fbest;
    computeMinorTics();
  }

  /**
   * Gets the number of major tics.
   * @return the number of major tics.
   */
  public int getCountMajor() {
    return _ntic;
  }

  /**
   * Gets major tic interval.
   * @return the major tic interval.
   */
  public double getDeltaMajor() {
    return _dtic;
  }

  /**
   * Gets the value of the first major tic.
   * @return the value of the first major tic.
   */
  public double getFirstMajor() {
    return _ftic;
  }

  /**
   * Gets the number of minor tics.
   * @return the number of minor tics.
   */
  public int getCountMinor() {
    return _nticMinor;
  }

  /**
   * Gets minor tic interval.
   * @return the minor tic interval.
   */
  public double getDeltaMinor() {
    return _dticMinor;
  }

  /**
   * Gets the value of the first minor tic.
   * @return the value of the first minor tic.
   */
  public double getFirstMinor() {
    return _fticMinor;
  }

  /**
   * Gets the tic multiple. The tic multiple is the number of (major 
   * and minor) tics per major tic, except near the ends of the axis.
   * @return the tic multiple.
   */
  public double getMultiple() {
    return _mtic;
  }

  ///////////////////////////////////////////////////////////////////////////
  // private

  private double _xmin;
  private double _xmax;
  private int _mtic;
  private int _ntic;
  private double _dtic;
  private double _ftic;
  private int _nticMinor;
  private double _dticMinor;
  private double _fticMinor;

  private static final int[] _mult = {1,2,5,10};

  private void computeMultiple() {
    _mtic = 1;
    double l10 = log10(_dtic/10.0);
    double l5 = log10(_dtic/5.0);
    double l2 = log10(_dtic/2.0);
    if (almostEqual(rint(l10),l10)) {
      _mtic = 10;
    } else if (almostEqual(rint(l5),l5)) {
      _mtic = 5;
    } else if (almostEqual(rint(l2),l2)) {
      _mtic = 2;
    }
  }

  private void computeMinorTics() {
    double dm = _dtic/_mtic;
    double fm = _ftic;
    while (_xmin<=fm-dm)
      fm -= dm;
    int nm = 1+(int)((_xmax-fm)/dm);
    _nticMinor = nm;
    _dticMinor = dm;
    _fticMinor = fm;
  }

  private static boolean almostEqual(double x1, double x2) {
    return abs(x1-x2)<=max(abs(x1),abs(x2))*100.0*M.DBL_EPSILON;
  }
}
