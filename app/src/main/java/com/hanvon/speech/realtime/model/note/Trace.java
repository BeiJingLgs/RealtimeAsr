package com.hanvon.speech.realtime.model.note;

import android.graphics.Point;
import android.graphics.Rect;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Trace {
	/** 不超过5个像素则视为点击 */
	private static final int POINT_THRESHOLD = 5;

/*	public static int PENCIL_TRACE = 0; // 铅笔
	public static int PEN_TRACE = 1; // 钢笔
	public static int BRUSH_TRACE = 2; // 毛笔
	public static int PRESSURE_TRACE = 3; // 压感笔*/
	private int width; // 笔迹的粗细
	//private int level; // 笔迹的灰度级
	//private int type; // 笔迹的类型，0表示铅笔，1表示钢笔，2表示毛笔，3表示压感笔
	public ArrayList<Point> points = null; // 笔迹的点
	public boolean bHasRec = false; // 本条笔迹的时候是否有录音
	public Record RecInfo = null; // 录音信息

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
/*
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}*/

	public int getCount() {
		// TODO Auto-generated method stub
		if (points == null) {
			return 0;
		}
		return points.size();
	}

	public Point getAt(int index) {
		// TODO Auto-generated method stub
		if (points == null) {
			return null;
		}
		return points.get(index);
	}

	public void addPoint(Point pt) {
		// TODO Auto-generated method stub
		if (points == null) {
			points = new ArrayList<Point>();
		}
		points.add(pt);
	}

	/**
	 * 初始化笔迹的数组，用于从文件读取以后的转换
	 *
	 * @param data
	 * @return 是否成功
	 */
	public boolean initialPenTrace(byte[] data) {
		// TODO Auto-generated method stub
		if (data.length < 4 * 2 * 2) {
			return false;
		}
		if (points == null) {
			points = new ArrayList<Point>();
		}

		// 得到点的个数,4个byte为一个int，2个int是一个点,最后一个是压感
		int count = data.length / (4 * 2);
		// 如果长度不正确，则重新new
		if (points.size() > 0) {
			points = new ArrayList<Point>();
		}

		// 设置记录的点
		for (int i = 0; i < count; i++) {
			Point pt = new Point();
			pt.x = NoteBaseData.byteToInt(data, i * 2 * 4, true);
			pt.y = NoteBaseData.byteToInt(data, i * 2 * 4 + 4, true);
			points.add(pt);
		}
		return true;
	}

	/**
	 * 把当前集合转换成byte数组，用于文件的存储
	 *
	 * @return 返回转换完成后的数组，若失败则返回null
	 */
	public byte[] convertPointArray() {
		int count = points.size();
		byte[] data = new byte[count * 2 * 4];
		for (int i = 0; i < count; i++) {
			Point pt = points.get(i);
			NoteBaseData.intToByte(data, i * 2 * 4, pt.x, true);
			NoteBaseData.intToByte(data, i * 2 * 4 + 4, pt.y, true);
		}
		return data;
	}

	/**
	 * 得到当前的的一条笔迹
	 *
	 * @param stream
	 *            文件对象
	 * @return
	 */
	public static Trace getCurTrace(FileInputStream stream) {
		// TODO Auto-generated method stub
		if (stream == null) {
			return null;
		}
		Trace trace = new Trace();
		try {
			byte[] info = new byte[4];
			// 笔迹的粗细
			stream.read(info, 0, 4);
			trace.setWidth(NoteBaseData.byteToInt(info, true));

		/*	// 笔迹的灰度级
			stream.read(info, 0, 4);
			trace.setLevel(NoteBaseData.byteToInt(info, true));

			// 笔迹的类型
			stream.read(info, 0, 4);
			trace.setType(NoteBaseData.byteToInt(info, true));*/

			// 笔迹的点数
			stream.read(info, 0, 4);
			int pointCount = NoteBaseData.byteToInt(info, true);



			// 点,记录压力值
			byte[] points = new byte[pointCount * 4 * 2];
			stream.read(points, 0, points.length);
			trace.initialPenTrace(points);
			points = null;

	// 录音信息
			// 读取录音信息
			stream.read(info, 0, 4);
			int nHasRec = NoteBaseData.byteToInt(info, true);
			if (nHasRec > 0) {
				trace.bHasRec = true;
			}
			if (trace.bHasRec) {
				trace.RecInfo = new Record();
				// 录音开始时间，ms为单位
				stream.read(info, 0, 4);
				trace.RecInfo.nTimeBegin = NoteBaseData.byteToInt(info, true);

				// 录音结束时间，ms为单位
				stream.read(info, 0, 4);
				trace.RecInfo.nTimeEnd = NoteBaseData.byteToInt(info, true);
}
			info = null;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return trace;
	}

	/**
	 * 把封面保存到文件中
	 *
	 * @param stream
	 *            要保存的文件流
	 * @return 返回是否成功
	 */
	public boolean saveToFile(FileOutputStream stream) {
		// TODO Auto-generated method stub
		if (stream == null) {
			return false;
		}
		byte[] info = new byte[4];

		try {
			// 笔迹的粗细
			NoteBaseData.intToByte(info, 0, getWidth(), true);
			stream.write(info, 0, 4);

		/*	// 笔迹的灰度级
			NoteBaseData.intToByte(info, 0, getLevel(), true);
			stream.write(info, 0, 4);

			// 笔迹的类型
			NoteBaseData.intToByte(info, 0, getType(), true);
			stream.write(info, 0, 4);*/

			// 笔迹的点的个数
			NoteBaseData.intToByte(info, 0, (points == null) ? 0 : points.size(),
					true);
			stream.write(info, 0, 4);

			// 笔迹的内容
			byte[] buffer = convertPointArray();
			stream.write(buffer, 0, buffer.length);
			// 录音信息
			// 录音的条数
			if (bHasRec) {
				NoteBaseData.intToByte(info, 0, 1, true);
			} else {
				NoteBaseData.intToByte(info, 0, 0, true);
			}
			stream.write(info, 0, 4);
	       if (bHasRec) {
				// 起始时间
			    NoteBaseData.intToByte(info, 0, (int)RecInfo.nTimeBegin, true);
				stream.write(info, 0, 4);
			    NoteBaseData.intToByte(info, 0, (int)RecInfo.nTimeEnd, true);
				stream.write(info, 0, 4);
			}
			
			info = null;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 获取一个四边形的外接矩形
	 * 
	 * @return 外接矩形
	 */
	public static Rect getQuadBorderRect(Point[] quad) {
		// TODO Auto-generated method stub
		if (quad == null || quad.length != 4) {
			return null;
		}

		Rect rt = new Rect(quad[0].x, quad[0].y, quad[0].x, quad[0].y);

		for (int i = 1; i < quad.length; i++) {
			rt.left = Math.min(rt.left, quad[i].x);
			rt.top = Math.min(rt.top, quad[i].y);
			rt.right = Math.max(rt.right, quad[i].x);
			rt.bottom = Math.max(rt.bottom, quad[i].y);
		}
		return rt;
	}

	/**
	 * 获取笔迹的外接矩形
	 * 
	 * @return 外接矩形
	 */
	public Rect getBorderRect() {
		// TODO Auto-generated method stub
		if (points == null || points.size() == 0) {
			return null;
		}
		Point pt = points.get(0);

		Rect rt = new Rect(pt.x, pt.y, pt.x, pt.y);

		for (int i = 1; i < points.size(); i++) {
			rt.left = Math.min(rt.left, points.get(i).x);
			rt.top = Math.min(rt.top, points.get(i).y);
			rt.right = Math.max(rt.right, points.get(i).x);
			rt.bottom = Math.max(rt.bottom, points.get(i).y);
		}
		return rt;
	}

	/**
	 * 得到Point类型的点
	 *
	 * @return
	 */
	public Point getPointAt(int index) {
		// TODO Auto-generated method stub
		if (points == null || points.size() == 0) {
			return null;
		}
		Point pt = new Point(points.get(index).x, points.get(index).y);
		return pt;
	}

	/**
	 * 判断某条笔迹和哪些笔迹相交
	 * 
	 * @param traces
	 *            笔迹的集合
	 * @param curTrace
	 *            某条要被判断的笔迹
	 * @return 相交的笔迹的索引的集合
	 */
	public static ArrayList<Integer> getIntersect(ArrayList<Trace> traces,
			Trace curTrace) {
		// TODO Auto-generated method stub
		if (traces == null || traces.size() == 0 || curTrace == null
				|| curTrace.getCount() == 0) {
			return null;
		}
		ArrayList<Integer> intersects = new ArrayList<Integer>();

		// 笔迹的外接矩形
		Rect borderIn = curTrace.getBorderRect();

		for (int i = 0; i < traces.size(); i++) {
			// 已经存在的笔迹的外接矩形
			Rect src = traces.get(i).getBorderRect();

			// 若两矩形相交才继续计算
			if (Rect.intersects(src, borderIn)) {
				Trace srcTrace = traces.get(i);

				for (int j = 0; j < curTrace.getCount() - 1; j++) {
					Rect minIn = new Rect(Math.min(curTrace.getAt(j).x,
							curTrace.getAt(j + 1).x), Math.min(
							curTrace.getAt(j).y, curTrace.getAt(j + 1).y),
							Math.max(curTrace.getAt(j).x,
									curTrace.getAt(j + 1).x), Math.max(
									curTrace.getAt(j).y,
									curTrace.getAt(j + 1).y));
					// 看当前的点和下一个点组成的小矩形是否和要比较的矩形相交，不想交则继续判断
					if (!Rect.intersects(minIn, src)) {
						continue;
					}
					boolean isIntersect = false;

					// 已经存在的笔迹
					for (int k = 0; k < srcTrace.getCount() - 1; k++) {
						// 如果相交退出本次循环
						if (isLineSegmentIntersect(curTrace.getPointAt(j),
								curTrace.getPointAt(j + 1),
								srcTrace.getPointAt(k),
								srcTrace.getPointAt(k + 1))) {
							isIntersect = intersects.add(new Integer(i));
							break;
						}
					}
					if (isIntersect) {
						break;
					}
				}
			}
		}
		return intersects;
	}

	/**
	 * 判断当前四边形和哪些笔迹相交
	 * 
	 * @param traces
	 *            笔迹的集合
	 * @return 相交的笔迹的索引的集合
	 */
	public static ArrayList<Integer> getIntersect2(ArrayList<Trace> traces,
			Point[] curQuad) {
		if (traces == null || traces.size() == 0 || curQuad == null
				|| curQuad.length != 4) {
			return null;
		}
		ArrayList<Integer> intersects = new ArrayList<Integer>();
		Rect borderIn = getQuadBorderRect(curQuad);

		for (int i = 0; i < traces.size(); i++) {
			// 已经存在的笔迹的外接矩形
			Rect src = traces.get(i).getBorderRect();
			// 若两矩形相交才继续计算
			if (Rect.intersects(src, borderIn)) {
				Trace srcTrace = traces.get(i);

				if (srcTrace.getCount() == 1) {
					if (IsLineIntersectRect(srcTrace.getPointAt(0),
							srcTrace.getPointAt(0), curQuad)) {
						intersects.add(new Integer(i));
					}
				} else {
					for (int j = 0; j < srcTrace.getCount() - 1; j++) {
						// 两个临近点连成一条直线，然后与四边形判断是否相交
						if (IsLineIntersectRect(srcTrace.getPointAt(j),
								srcTrace.getPointAt(j + 1), curQuad)) {
							intersects.add(new Integer(i));
							break;
						}
					}
				}
			}
		}
		return intersects;
	}

	/**
	 * 判断两条线段是否相交
	 * 
	 * @param pt1
	 *            第一条线段的起点
	 * @param pt2
	 *            第一条线段的终点
	 * @param pt3
	 *            第二条线段的起点
	 * @param pt4
	 *            第二条线段的终点
	 * @return 返回true和false
	 */
	private static boolean isLineSegmentIntersect(Point pt1, Point pt2,
			Point pt3, Point pt4) {
		// TODO Auto-generated method stub
		Rect rt1 = new Rect(Math.min(pt1.x, pt2.x), Math.min(pt1.y, pt2.y),
				Math.max(pt1.x, pt2.x), Math.max(pt1.y, pt2.y));
		Rect rt2 = new Rect(Math.min(pt3.x, pt4.x), Math.min(pt3.y, pt4.y),
				Math.max(pt3.x, pt4.x), Math.max(pt3.y, pt4.y));
		if (!Rect.intersects(rt1, rt2)) { // 外接矩形不相交则肯定不相交
			return false;
		}
		// 线段相交判断：设线段为[segA1,segA2]，[segB1,segB2]，有跨立公式：
		// (segA1 - segB1) X (segB2 - segB1) * (segB2 - segB1) X (segA2 - segB1)
		// >= 0
		// (segB1 - segA1) X (segA2 - segA1) * (segA2 - segA1) X (segB2 - segA1)
		// >= 0
		// A X B为矢量叉乘 ，相当于A.x*B.y - B.x*A.y; 结果>0:A到B顺时针；<0:A到B逆时针；=0:A与B共线
		int result = vectorCross(pointOrigin(pt1, pt3), pointOrigin(pt4, pt3))
				* vectorCross(pointOrigin(pt4, pt3), pointOrigin(pt2, pt3));

		if (result < 0) {
			return false;
		}
		result = vectorCross(pointOrigin(pt3, pt1), pointOrigin(pt2, pt1))
				* vectorCross(pointOrigin(pt2, pt1), pointOrigin(pt4, pt1));
		if (result < 0) {
			return false;
		}
		// 由于已经判断过外接矩形，所以不用再判断==0的情况了
		return true;
	}

	/**
	 * 判断线段是否和四边形相交
	 * 
	 * @param pt1
	 *            第一条线段的起点
	 * @param pt2
	 *            第一条线段的终点
	 * @param rect
	 *            四边形的四个点，必须是顺时针或者逆时针给定的四个点，不能交叉
	 * @return 返回true和false
	 */
	private static boolean IsLineIntersectRect(Point pt1, Point pt2,
			Point[] rect) {
//		Log.d("erase", "Point 1 （" + pt1.x + "," + pt1.y + ") " + " Point 2 （"
//				+ pt2.x + "," + pt2.y + ") ");

		// 判断两个点是否在矩形上或内部
		if (pInQuadrangle(rect[0], rect[1], rect[2], rect[3], pt1)) {
			//Log.d("erase", "true 1");
			return true;
		}
		if (pInQuadrangle(rect[0], rect[1], rect[2], rect[3], pt2)) {
			//Log.d("erase", "true 2");
			return true;
		}

		// 四边形的每个边是否和线段相交
		if (isLineSegmentIntersect(pt1, pt2, rect[0], rect[1]))
			return true;
		if (isLineSegmentIntersect(pt1, pt2, rect[1], rect[2]))
			return true;
		if (isLineSegmentIntersect(pt1, pt2, rect[2], rect[3]))
			return true;
		if (isLineSegmentIntersect(pt1, pt2, rect[3], rect[0]))
			return true;

		return false;
	}

	/**
	 * 点是否在四边形内 参数a,b,c,d分别是四边形的四个点，判断p是否在这个四边形内
	 */
	public static boolean pInQuadrangle(Point a, Point b, Point c, Point d,
			Point p) {
		double dTriangle = triangleArea(a, b, p) + triangleArea(b, c, p)
				+ triangleArea(c, d, p) + triangleArea(d, a, p);
		double dQuadrangle = triangleArea(a, b, c) + triangleArea(c, d, a);
		return dTriangle == dQuadrangle;
	}

	// 返回三个点组成三角形的面积
	private static double triangleArea(Point a, Point b, Point c) {
		double result = Math.abs((a.x * b.y + b.x * c.y + c.x * a.y - b.x * a.y
				- c.x * b.y - a.x * c.y) / 2);
		return result;
	}

	/**
	 * 计算以ptOrigin为原点的坐标
	 * 
	 * @param pt
	 *            要计算的点
	 * @param ptOrigin
	 *            原点参照点
	 * @return 以ptOrigin为原点的坐标
	 */
	public static Point pointOrigin(Point pt, Point ptOrigin) {
		return new Point(pt.x - ptOrigin.x, pt.y - ptOrigin.y);
	}

	/**
	 * 矢量叉乘
	 * 
	 * @param pt1
	 * @param pt2
	 * @return >0:pt1到pt2顺时针；<0:pt1到pt2逆时针；=0:pt1与pt2共线
	 */
	public static int vectorCross(Point pt1, Point pt2) {
		return (pt1.x * pt2.y - pt2.x * pt1.y);
	}

	public void clear() {
		// TODO Auto-generated method stub
		if (points != null) {
			for (int i = points.size() - 1; i >= 0; i--) {
				points.remove(i);
			}
			points.clear();
			points = null;
		}
	}

	/**
	 * 深拷贝当前的笔迹
	 * 
	 * @return 返回新构建的笔迹
	 */
	public Trace deepClone() {
		// TODO Auto-generated method stub
		Trace newTrace = new Trace();
		newTrace.width = this.width;
        newTrace.bHasRec = this.bHasRec;

		if (newTrace.bHasRec) {
			newTrace.RecInfo = new Record( this.RecInfo.nTimeBegin,
					this.RecInfo.nTimeEnd);
		}

		for (Point pt : points) {
			Point newPt = new Point(pt.x, pt.y);
			newTrace.addPoint(newPt);
		}
		return newTrace;
	}

	/**
	 * 当前笔迹是否可以看成是一点
	 * 
	 * @return
	 */
	public boolean isPoint() {
		Rect rt = getBorderRect();
		if (rt.right - rt.left <= POINT_THRESHOLD
				&& rt.bottom - rt.top <= POINT_THRESHOLD) {
			return true;
		}
		return false;
	}

	/**
	 * 判断某次点击的区域和哪些笔迹相交,目前主要判断是否和点相交
	 * 
	 * @param traces
	 *            笔迹的集合
	 * @param curTrace
	 *            某条要被判断的笔迹
	 * @param pointWidth
	 * @return 相交的笔迹的索引的集合
	 */
	public static ArrayList<Integer> getPointIntersect(ArrayList<Trace> traces,
                                                       Trace curTrace, int pointWidth) {
		// TODO Auto-generated method stub
		if (traces == null || traces.size() == 0 || curTrace == null
				|| curTrace.getCount() == 0) {
			return null;
		}
		ArrayList<Integer> intersects = new ArrayList<Integer>();

		// 计算出该点的外接矩形
		Rect rtBorder = curTrace.getBorderRect();
		;

		rtBorder.left = (rtBorder.left - pointWidth) > 0 ? (rtBorder.left - pointWidth)
				: 0;
		rtBorder.top = (rtBorder.top - pointWidth) > 0 ? (rtBorder.top - pointWidth)
				: 0;
		rtBorder.bottom += pointWidth;
		rtBorder.right += pointWidth;

		for (int i = 0; i < traces.size(); i++) {
			if (traces.get(i).isPoint()) {
				// 已经存在的笔迹的外接矩形
				Rect src = traces.get(i).getBorderRect();

				// 若两矩形相交就视为相交
				if (Rect.intersects(src, rtBorder)) {
					intersects.add(new Integer(i));
				}
			}
		}
		return intersects;
	}
	/**
	 * 判断某次点击的区域和哪些笔迹相交,听录音点击用，判断条件宽松
	 * 
	 * @param traces
	 *            笔迹的集合
	 * @param curTrace
	 *            某条要被判断的笔迹
	 * @param pointWidth
	 * @return 相交的笔迹的索引的集合
	 */
	public static ArrayList<Integer> getPointInterTrace(
            ArrayList<Trace> traces, Trace curTrace, int pointWidth) {
		// TODO Auto-generated method stub
		if (traces == null || traces.size() == 0 || curTrace == null
				|| curTrace.getCount() == 0) {
			return null;
		}
		ArrayList<Integer> intersects = new ArrayList<Integer>();

		// 计算出该点的外接矩形
		Rect rtBorder = curTrace.getBorderRect();
		;

		rtBorder.left = (rtBorder.left - pointWidth) > 0 ? (rtBorder.left - pointWidth)
				: 0;
		rtBorder.top = (rtBorder.top - pointWidth) > 0 ? (rtBorder.top - pointWidth)
				: 0;
		rtBorder.bottom += pointWidth;
		rtBorder.right += pointWidth;

		for (int i = 0; i < traces.size(); i++) {
			// 已经存在的笔迹的外接矩形
			Rect src = traces.get(i).getBorderRect();

			// 若两矩形相交就视为相交
			if (Rect.intersects(src, rtBorder)) {
				intersects.add(new Integer(i));
			}
		}
		return intersects;
	}
}
