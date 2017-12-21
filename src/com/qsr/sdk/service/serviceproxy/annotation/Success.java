package com.qsr.sdk.service.serviceproxy.annotation;

public enum Success {

	NotNull {
		@Override
		public boolean isSuccess(Object object) {
			return object != null;
		}
	},
	IsNull {
		@Override
		public boolean isSuccess(Object object) {
			return object == null;
		}
	},
	IsZero {
		@Override
		public boolean isSuccess(Object object) {
			if (object instanceof Number) {
				return ((Number) object).intValue() == 0;
			}
			return false;
		}
	},
	NotZero {
		@Override
		public boolean isSuccess(Object object) {
			if (object instanceof Number) {
				return ((Number) object).intValue() != 0;
			}
			return false;
		}
	},
	GtZero {
		@Override
		public boolean isSuccess(Object object) {

			if (object instanceof Number) {
				return ((Number) object).intValue() > 0;
			}

			return false;
		}
	},

	Ignore {
		@Override
		public boolean isSuccess(Object object) {
			return true;
		}
	};

	public abstract boolean isSuccess(Object object);
}
