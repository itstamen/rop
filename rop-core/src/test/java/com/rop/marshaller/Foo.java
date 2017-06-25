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

import javax.xml.bind.annotation.XmlRootElement;

/**
* @author : chenxh(quickselect@163.com)
* @date: 14-4-21
*/
@XmlRootElement
public class Foo implements IFoo{
    private Boolean b1;

    private boolean b2;

    private Integer i1;

    private int i2;

    private String ok;

    public Boolean getB1() {
        return b1;
    }

    public void setB1(Boolean b1) {
        this.b1 = b1;
    }

    public boolean isB2() {
        return b2;
    }

    public void setB2(boolean b2) {
        this.b2 = b2;
    }

    public Integer getI1() {
        return i1;
    }


    public void setI1(Integer i1) {
        this.i1 = i1;
    }

    public int getI2() {
        return i2;
    }

    public void setI2(int i2) {
        this.i2 = i2;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((b1 == null) ? 0 : b1.hashCode());
		result = prime * result + (b2 ? 1231 : 1237);
		result = prime * result + ((i1 == null) ? 0 : i1.hashCode());
		result = prime * result + i2;
		result = prime * result + ((ok == null) ? 0 : ok.hashCode());
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
		Foo other = (Foo) obj;
		if (b1 == null) {
			if (other.b1 != null)
				return false;
		} else if (!b1.equals(other.b1))
			return false;
		if (b2 != other.b2)
			return false;
		if (i1 == null) {
			if (other.i1 != null)
				return false;
		} else if (!i1.equals(other.i1))
			return false;
		if (i2 != other.i2)
			return false;
		if (ok == null) {
			if (other.ok != null)
				return false;
		} else if (!ok.equals(other.ok))
			return false;
		return true;
	}
}
