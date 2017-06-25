/*
 * Copyright 2012-2016 the original author or authors.
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
package com.rop.marshaller;

/**
 * @author : chenxh(quickselect@163.com)
 * @date: 14-4-21
 */
public class Bar {

    private String fld1 = "a";

    private String fld2 = "b";

    private Foo foo;

    public String getFld1() {
        return fld1;
    }

    public void setFld1(String fld1) {
        this.fld1 = fld1;
    }

    public String getFld2() {
        return fld2;
    }

    public void setFld2(String fld2) {
        this.fld2 = fld2;
    }

    public Foo getFoo() {
        return foo;
    }

    public void setFoo(Foo foo) {
        this.foo = foo;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fld1 == null) ? 0 : fld1.hashCode());
		result = prime * result + ((fld2 == null) ? 0 : fld2.hashCode());
		result = prime * result + ((foo == null) ? 0 : foo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bar other = (Bar) obj;
		if (fld1 == null) {
			if (other.fld1 != null)
				return false;
		} else if (!fld1.equals(other.fld1))
			return false;
		if (fld2 == null) {
			if (other.fld2 != null)
				return false;
		} else if (!fld2.equals(other.fld2))
			return false;
		if (foo == null) {
			if (other.foo != null)
				return false;
		} else if (!foo.equals(other.foo))
			return false;
		return true;
	}
}
