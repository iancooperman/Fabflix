import csv
import sys


def average(numbers: list) -> float:
    return sum(numbers) / len(numbers)


def main() -> None:
    try:
        ts_values = []
        tj_values = []


        log_file_names = sys.argv[1:]
        print("Using files:")
        for file_name in log_file_names:
            with open(file_name) as f:
                reader = csv.reader(f, delimiter=',')

                row_count = 0
                for row in reader:
                    row_count += 1
                    # print(row)
                    ts = float(row[0])
                    tj = float(row[1])

                    ts_values.append(ts)
                    tj_values.append(tj)

            print(f"\t{file_name} ({row_count} rows)")


        ts_average = average(ts_values)
        tj_average = average(tj_values)

        print(f"Average TS (ms): {ts_average}")
        print(f"Average TJ (ms): {tj_average}")

    except Exception as e:
        print(e)


    input("Press ENTER to quit.")


main()
