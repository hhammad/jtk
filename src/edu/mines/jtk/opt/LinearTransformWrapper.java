/****************************************************************************
Copyright (c) 2004, Landmark Graphics and others. All rights reserved.
This program and accompanying materials are made available under the terms of
the Common Public License - v1.0, which accompanies this distribution, and is
available at http://www.eclipse.org/legal/cpl-v10.html
****************************************************************************/
package edu.mines.jtk.opt;

/** Wrap a LinearTransform as a non-linear Transform,
    by ignoring reference model.
    @author W.S. Harlan
*/
public class LinearTransformWrapper implements Transform {
  private LinearTransform _linearTransform = null;

  /** Constructor.
      @param linearTransform Wrap this as a general Transform
  */
  public LinearTransformWrapper(LinearTransform linearTransform) {
    _linearTransform = linearTransform;
  }

  public void forwardNonlinear(Vect data, VectConst model) {
    _linearTransform.forward(data, model);
  }

  public void forwardLinearized(Vect data,
                                VectConst model,
                                VectConst modelReference) {
    _linearTransform.forward(data, model);
  }

  public void addTranspose(VectConst data,
                           Vect model,
                           VectConst modelReference) {
    _linearTransform.addTranspose(data, model);
  }

  public void inverseHessian(Vect model, VectConst modelReference) {
    _linearTransform.inverseHessian(model);
  }

  public void adjustRobustErrors(Vect dataError) {
    _linearTransform.adjustRobustErrors(dataError);
  }
}