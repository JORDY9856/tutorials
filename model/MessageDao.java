package guest.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageDao {

	// Single Pattern
	private static MessageDao instance;

	// DB 연결시 관한 변수
	private static final String dbDriver = "oracle.jdbc.driver.OracleDriver";
	private static final String dbUrl = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
	private static final String dbUser = "hr";
	private static final String dbPass = "1234";

	// --------------------------------------------
	// ##### 객체 생성하는 메소드
	public static MessageDao getInstance() throws MessageException {
		if (instance == null) {
			instance = new MessageDao();
		}
		return instance;
	}

	private MessageDao() throws MessageException {

		try {

			Class.forName(dbDriver);
			/********************************************
			 * 1. 오라클 드라이버를 로딩 ( DBCP 연결하면 삭제할 부분 )
			 */

		} catch (Exception ex) {
			throw new MessageException("방명록 ) DB 연결시 오류  : " + ex.toString());
		}

	}

	/*
	 * 메세지를 입력하는 함수
	 */
	public void insert(Message rec) throws MessageException {
		Connection con = null;
		PreparedStatement ps = null;
		try {

			// 1. 연결객체(Connection) 얻어오기
			con = DriverManager.getConnection(dbUrl, dbUser, dbPass);

			// 2. sql 문장 만들기
			String sql = "INSERT INTO GuestTB(message_Id,guest_Name, password, message)"
					+ "	VALUES(seq_guestTB_message_Id.nextval,?,?,?)";

			// String sql="INSERT INTO GuestTB
			// VALUES(seq_guestTB_message_Id.nextval,?,?,?)";

			// 3. 전송객체 얻어오기
			ps = con.prepareStatement(sql);

		/*	ps.setString(1, rec.getGuest_Name());
			ps.setString(2, rec.getPassword());
			ps.setString(3, rec.getMessage());
*/
			
			
			  ps.setString(1, rec.getGuest_Name()); 
			  ps.setString(2, rec.getPassword());
			  ps.setString(3, rec.getMessage());
			 

			// 4. 전송하기
			ps.executeUpdate();

		} catch (Exception ex) {
			throw new MessageException("방명록 ) DB에 입력시 오류  : " + ex.toString());
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ex) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException ex) {
				}
			}
		}

	}

	/*
	 * 메세지 목록 전체를 얻어올 때
	 */
	public List<Message> selectList() throws MessageException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Message> mList = new ArrayList<Message>();
		boolean isEmpty = true;

		try {

			// 1.connection 얻어오기
			con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
			
			// 2. sql 문장
			String sql = "SELECT * FROM guesttb";
			
			// 3.전송객체
			ps = con.prepareStatement(sql);
			
			// 4.전송
			rs = ps.executeQuery();

			// 레코드가 있는지 없는지 한칸 내려가야 알 수 있다.
			// 한칸만 내려와서 끝나는게 아니라 끝까지 진행이 되어야 하기 때문에
			// if가 아니라 while문으로 간다

			while (rs.next()) {
				Message m = new Message();
				m.setMessage_Id(rs.getInt("Message_Id"));
				m.setGuest_Name(rs.getString("guest_Name"));
				m.setMessage(rs.getString("Message"));

				mList.add(m);// 블록안에서 만들어진 변수는 블록을 벗어나면 사라진다
				isEmpty = false;
			}


			if (isEmpty)
				return Collections.emptyList(); // isEmpty 불린이 비어있는지 확인하려고

			return mList;
		} catch (Exception ex) {
			throw new MessageException("방명록 ) DB에 목록 검색시 오류  : " + ex.toString());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ex) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException ex) {
				}
			}
		}
	}

	/*
	 * ----------------------------------------- 현재 페이지에 보여울 메세지 목록
	 * 얻어올 때
	 */
	public List<Message> selectList(int firstRow, int endRow) throws MessageException {
		Connection con = null;

		PreparedStatement ps = null;

		ResultSet rs = null;

		List<Message> mList = new ArrayList<Message>();

		boolean isEmpty = true;

		try {
			// 1.connection 얻어오기
						con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
						// 2. sql 문장
						String sql = " select *\r\n" + 
								"    from guesttb\r\n" + 
								"    where message_id IN ( select message_id \r\n" + 
								"    from (select rownum as rnum ,message_id \r\n" + 
								"        from (select message_id from guesttb order by message_id desc))\r\n" + 
								"    where rnum>=? and rnum<=? )\r\n" + 
								"    order by message_id desc";
						// 3.전송객체
						ps = con.prepareStatement(sql);
			
						ps.setInt(1, firstRow);
						ps.setInt(2, endRow);
			
						// 4.전송
						rs = ps.executeQuery();

						// 레코드가 있는지 없는지 한칸 내려가야 알 수 있다.
						// 한칸만 내려와서 끝나는게 아니라 끝까지 진행이 되어야 하기 때문에
						// if가 아니라 while문으로 간다

						while (rs.next()) {
							Message m = new Message();
							m.setMessage_Id(rs.getInt("Message_Id"));
							m.setGuest_Name(rs.getString("guest_Name"));
							m.setMessage(rs.getString("Message"));

							mList.add(m);// 블록안에서 만들어진 변수는 블록을 벗어나면 사라진다
							isEmpty = false;
						}
						return mList;

			
			/*
			con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
			 * String sql = "SELECT * FROM listMessage";
			 * 
			 * ps = con.prepareStatement(sql);
			 * 
			 * rs = ps.executeQuery();
			 * 
			 * while (rs.next()) { Message message = new Message();
			 * message.setGuest_Name(rs.getString("guest_Name"));
			 * message.setGuest_Name(rs.getString("password"));
			 * message.setGuest_Name(rs.getString("message")); mList.add(message); }
			if (isEmpty)
				return Collections.emptyList();

			return mList;
			 */

		} catch (Exception ex) {
			throw new MessageException("방명록 ) DB에 목록 검색시 오류  : " + ex.toString());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ex) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException ex) {
				}
			}
		}
	}

	/*
	 * ----------------------- 메세지 전체 레코드 수를 검색
	 */

	public int getTotalCount() throws MessageException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;

		try {
			con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
			
			String sql = "SELECT count(*) AS cnt FROM guestTB";//얻어올 때는 select, 
													//count(*)의 별칭지정
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			
			if(rs.next()) { //한칸을 내려와야한다.(다음이 있으면 반드시 가져오겠다)
				    
				count = rs.getInt("CNT");
			}
			
			
			return count;

		} catch (Exception ex) {
			throw new MessageException("방명록 ) DB에 목록 검색시 오류  : " + ex.toString());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ex) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException ex) {
				}
			}
		}
	}

	/*
	 * 메세지 번호와 비밀번호에 의해 메세지 삭제
	 */
	public int delete(int message_Id, String password) throws MessageException {
		int result = 0;
		Connection con = null;
		PreparedStatement ps = null;
		try {

			con = DriverManager.getConnection(dbUrl, dbUser, dbPass);

			String sql = "DELETE FROM guesttb where message_Id =?	" + "	AND password=?";

			ps = con.prepareStatement(sql);
			ps.setInt(1, message_Id);
			ps.setString(2, password);

			result = ps.executeUpdate();

			return result;
	
		} catch (Exception ex) {
			throw new MessageException("방명록 ) DB에 삭제시 오류  : " + ex.toString());
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ex) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException ex) {
				}
			}
		}
	}
}
