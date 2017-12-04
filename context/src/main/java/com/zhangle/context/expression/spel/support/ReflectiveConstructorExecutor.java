/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhangle.context.expression.spel.support;



import com.zhangle.context.expression.AccessException;
import com.zhangle.context.expression.ConstructorExecutor;
import com.zhangle.context.expression.EvaluationContext;
import com.zhangle.context.expression.TypedValue;
import com.zhangle.lang.Nullable;
import com.zhangle.utils.ReflectionUtils;

import java.lang.reflect.Constructor;

/**
 * A simple ConstructorExecutor implementation that runs a constructor using reflective
 * invocation.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ReflectiveConstructorExecutor implements ConstructorExecutor {

	private final Constructor<?> ctor;

	@Nullable
	private final Integer varargsPosition;


	public ReflectiveConstructorExecutor(Constructor<?> ctor) {
		this.ctor = ctor;
		if (ctor.isVarArgs()) {
			Class<?>[] paramTypes = ctor.getParameterTypes();
			this.varargsPosition = paramTypes.length - 1;
		}
		else {
			this.varargsPosition = null;
		}
	}

	@Override
	public TypedValue execute(EvaluationContext context, Object... arguments) throws AccessException {
		try {
			ReflectionHelper.convertArguments(
					context.getTypeConverter(), arguments, this.ctor, this.varargsPosition);
			if (this.ctor.isVarArgs()) {
				arguments = ReflectionHelper.setupArgumentsForVarargsInvocation(
						this.ctor.getParameterTypes(), arguments);
			}
			ReflectionUtils.makeAccessible(this.ctor);
			return new TypedValue(this.ctor.newInstance(arguments));
		}
		catch (Exception ex) {
			throw new AccessException("Problem invoking constructor: " + this.ctor, ex);
		}
	}

	public Constructor<?> getConstructor() {
		return this.ctor;
	}

}
