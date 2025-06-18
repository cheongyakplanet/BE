import os
import re
import requests
import pymysql
from bs4 import BeautifulSoup

script_dir = os.path.dirname(os.path.abspath(__file__))    # .../src/main/scripts
main_dir   = os.path.dirname(script_dir)                    # .../src/main
prop_path  = os.path.join(main_dir, 'resources', 'properties', 'env.properties')

def load_properties(path):
    props = {}
    with open(path, encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            key, val = line.split('=', 1)
            props[key.strip()] = val.strip()
    return props

# ── 실제 로딩 ──
props = load_properties(prop_path)

MYSQL_HOST     = props['mysql.host']
MYSQL_PORT     = int(props.get('mysql.port', 3306))
MYSQL_USER     = props['mysql.user']
MYSQL_PASSWORD = props['mysql.password']
MYSQL_DB       = props['mysql.db']


def parse_supply_target(soup, subscription_info_id, house_manage_no):
    """
    '공급대상' 테이블 파싱
    """
    targets = []
    # h5 태그에서 "공급대상" 텍스트를 찾아 그 다음 테이블을 선택
    supply_target_h5 = soup.find("h5", string=lambda x: x and "공급대상" in x)
    if not supply_target_h5:
        return targets

    table = supply_target_h5.find_next("table")
    if not table:
        return targets

    tbody = table.find("tbody")
    if not tbody:
        return targets

    rows = tbody.find_all("tr")
    current_housing_category = None

    for row in rows:
        cells = row.find_all("td")

        # 합계 행 건너뛰기
        if cells and "계" in cells[0].get_text(strip=True):
            continue

        if len(cells) >= 6:  # 데이터 행
            if len(cells) == 7:  # housing_category 포함된 행
                current_housing_category = cells[0].get_text(strip=True)
                start_idx = 1
            else:  # housing_category 없는 행
                start_idx = 0

            housing_type = cells[start_idx].get_text(strip=True)
            supply_area = cells[start_idx + 1].get_text(strip=True)
            supply_count_normal = cells[start_idx + 2].get_text(strip=True)
            supply_count_special = cells[start_idx + 3].get_text(strip=True)
            supply_count_total = cells[start_idx + 4].get_text(strip=True)
            house_manage_no_detail = cells[start_idx + 5].get_text(strip=True)

            try:
                supply_area_val = float(supply_area.replace(',', '')) if supply_area else None
            except:
                supply_area_val = None
            try:
                supply_count_normal_val = int(supply_count_normal.replace(',', '')) if supply_count_normal else None
            except:
                supply_count_normal_val = None
            try:
                supply_count_special_val = int(supply_count_special.replace(',', '')) if supply_count_special else None
            except:
                supply_count_special_val = None
            try:
                supply_count_total_val = int(supply_count_total.replace(',', '')) if supply_count_total else None
            except:
                supply_count_total_val = None

            targets.append({
                'subscription_info_id': subscription_info_id,
                'house_manage_no': house_manage_no,
                'housing_category': current_housing_category,
                'housing_type': housing_type,
                'supply_area': supply_area_val,
                'supply_count_normal': supply_count_normal_val,
                'supply_count_special': supply_count_special_val,
                'supply_count_total': supply_count_total_val,
                'house_manage_no_detail': house_manage_no_detail
            })

    return targets

def parse_special_supply_target(soup, subscription_info_id, house_manage_no):
    """
    '특별공급 공급대상' 테이블 파싱
    """
    special_targets = []
    # h5 태그에서 "특별공급 공급대상" 텍스트를 찾아 그 다음 테이블을 선택
    special_target_h5 = soup.find("h5", string=lambda x: x and "특별공급 공급대상" in x)
    if not special_target_h5:
        return special_targets

    table = special_target_h5.find_next("table")
    if not table:
        return special_targets

    tbody = table.find("tbody")
    if not tbody:
        return special_targets

    rows = tbody.find_all("tr")
    for row in rows:
        cells = row.find_all("td")
        if len(cells) != 11:  # 주택형 + 9개 공급유형 + 계
            continue

        housing_type = cells[0].get_text(strip=True)

        # 각 공급유형별 세대수 파싱
        supply_counts = {}
        supply_types = ['multichild', 'newlywed', 'first', 'youth', 'elderly',
                       'newborn', 'institution_recommend', 'previous_institution', 'others', 'total']

        for i, supply_type in enumerate(supply_types, 1):
            try:
                count = int(cells[i].get_text(strip=True).replace(',', ''))
            except:
                count = None
            supply_counts[f'supply_count_{supply_type}'] = count

        special_targets.append({
            'subscription_info_id': subscription_info_id,
            'house_manage_no': house_manage_no,
            'housing_type': housing_type,
            **supply_counts
        })

    return special_targets

def parse_price_info(soup, subscription_info_id, house_manage_no):
    """
    '공급금액' 테이블 파싱
    """
    price_infos = []
    # h5 태그에서 "공급금액" 텍스트를 찾아 그 다음 테이블을 선택
    price_h5 = soup.find("h5", string=lambda x: x and "공급금액" in x)
    if not price_h5:
        return price_infos

    table = price_h5.find_next("table")
    if not table:
        return price_infos

    tbody = table.find("tbody")
    if not tbody:
        return price_infos

    # 2순위 청약금 정보 찾기
    second_priority_cell = tbody.find("td", string=lambda x: x and "청약통장" in str(x))
    second_priority_payment = second_priority_cell.get_text(strip=True) if second_priority_cell else None

    rows = tbody.find_all("tr")
    for row in rows:
        cells = row.find_all("td")
        if len(cells) >= 2:
            housing_type = cells[0].get_text(strip=True)
            supply_price_text = cells[1].get_text(strip=True).replace(',', '')
            try:
                supply_price = int(supply_price_text)
            except:
                supply_price = None

            # 입주예정월 찾기
            move_in_month = None
            move_in_li = soup.find("li", string=lambda x: x and "입주예정월" in str(x))
            if move_in_li:
                text = move_in_li.get_text(strip=True)
                import re
                m = re.search(r"입주예정월\s*[:：]\s*([\d\.]+)", text)
                if m:
                    move_in_month = m.group(1)

            price_infos.append({
                'subscription_info_id': subscription_info_id,
                'house_manage_no': house_manage_no,
                'housing_type': housing_type,
                'supply_price': supply_price,
                'second_priority_payment': second_priority_payment,
                'move_in_month': move_in_month
            })

    return price_infos

def insert_data(conn, table, data):
    """
    conn: pymysql connection
    table: 삽입할 테이블 이름 (문자열)
    data: 딕셔너리의 리스트
    """
    if not data:
        return
    keys = data[0].keys()
    columns = ", ".join(keys)
    placeholders = ", ".join(["%s"] * len(keys))
    query = f"INSERT INTO {table} ({columns}) VALUES ({placeholders})"
    with conn.cursor() as cursor:
        for row in data:
            values = tuple(row[k] for k in keys)
            cursor.execute(query, values)
    conn.commit()

def main():
    # MySQL RDS 연결
    conn = pymysql.connect(
        host=MYSQL_HOST,
        port=MYSQL_PORT,
        user=MYSQL_USER,
        password=MYSQL_PASSWORD,
        database=MYSQL_DB,
        charset='utf8mb4'
    )
    try:
        with conn.cursor() as cursor:
            # 1) subscription_price_info 에 이미 들어간 마지막 subscription_info_id 조회
            cursor.execute("SELECT COALESCE(MAX(subscription_info_id), 0) FROM subscription_price_info")
            max_processed_id = cursor.fetchone()[0] or 0
            start_id = max_processed_id + 1
            print(f"▶ 이미 처리된 마지막 ID: {max_processed_id} → 새로 처리할 ID >= {start_id}")

            # 2) subscription_info 중 start_id 이상인 행만 가져오기
            sql = ("SELECT id, pblanc_url, house_manage_no FROM subscription_info WHERE id >= %s")
            cursor.execute(sql, (start_id,))
            rows = cursor.fetchall()

            for row in rows:
                subscription_info_id = row[0]
                pblanc_url = row[1]
                house_manage_no = row[2]
                if not pblanc_url:
                    print(f"ID {subscription_info_id}: pblanc_url이 없습니다.")
                    continue

                try:
                    headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"}
                    response = requests.get(pblanc_url, headers=headers, timeout=10)

                    if response.status_code == 200:
                        html = response.text
                        soup = BeautifulSoup(html, 'html.parser')

                        # 공급대상 정보 파싱 및 삽입
                        supply_targets = parse_supply_target(soup, subscription_info_id, house_manage_no)
                        if supply_targets:
                            insert_data(conn, "subscription_supply_target", supply_targets)
                            print(f"ID {subscription_info_id} 공급대상 정보 삽입 완료: {len(supply_targets)} 건")
                        else:
                            print(f"ID {subscription_info_id} 공급대상 정보 없음")

                        # 특별공급 공급대상 정보 파싱 및 삽입
                        special_supply_targets = parse_special_supply_target(soup, subscription_info_id, house_manage_no)
                        if special_supply_targets:
                            insert_data(conn, "subscription_special_supply_target", special_supply_targets)
                            print(f"ID {subscription_info_id} 특별공급 공급대상 정보 삽입 완료: {len(special_supply_targets)} 건")
                        else:
                            print(f"ID {subscription_info_id} 특별공급 공급대상 정보 없음")

                        # 공급금액 및 입주예정월 정보 파싱 및 삽입
                        price_infos = parse_price_info(soup, subscription_info_id, house_manage_no)
                        if price_infos:
                            insert_data(conn, "subscription_price_info", price_infos)
                            print(f"ID {subscription_info_id} 공급금액 정보 삽입 완료: {len(price_infos)} 건")
                        else:
                            print(f"ID {subscription_info_id} 공급금액 정보 없음")
                    else:
                        print(f"ID {subscription_info_id}: URL 요청 실패, status code {response.status_code}")
                except Exception as e:
                    print(f"ID {subscription_info_id} 처리 중 오류 발생: {e}")

    finally:
        conn.close()

if __name__ == "__main__":
    main()